package org.example.paymentgateway.services;

import org.example.paymentgateway.dto.LoginRequest;
import org.example.paymentgateway.dto.UserInfoResponse;
import org.example.paymentgateway.dto.UserRegistration;
import org.example.paymentgateway.entities.Role;
import org.example.paymentgateway.entities.User;
import org.example.paymentgateway.entities.UserPrincipal;
import org.example.paymentgateway.enums.RiskLevel;
import org.example.paymentgateway.exception.UserAlreadyExistsException;
import org.example.paymentgateway.repositories.RoleRepository;
import org.example.paymentgateway.repositories.UserRepository;
import org.example.paymentgateway.security.JwtAuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final JwtAuthToken jwtAuthToken;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, RoleRepository roleRepository, UserRepository userRepository, JwtAuthToken jwtAuthToken, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.jwtAuthToken = jwtAuthToken;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * @param loginRequest object that comprises both the username and password
     * @return UserInfoResponse builder
     */
    public UserInfoResponse authenticateUser(LoginRequest loginRequest) {
        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtAuthToken.generateToken(userDetails);

        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return UserInfoResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .jwtToken(jwt)
                .roles(rolesList)
                .build();
    }

    public Map<String, Object> registerUser(UserRegistration userRegistration) {
        final Map<String, Object> response = new HashMap<>();
        if (userRegistration == null || userRegistration.getUsername() == null || userRegistration.getPassword() == null || !userRegistration.isPassWordMatch()) {
            response.put("status", false);
            response.put("message", "invalid validation data");
            return response;
        }
        try {
            final String username = userRegistration.getUsername();
            final String email = userRegistration.getEmail();

            if (userRepository.existsByUsername(username)) {
                logger.warn("username {} already exists", username);
                response.put("status", false);
                response.put("message", "username already exists");
                throw new UserAlreadyExistsException("user with this name already exists");
            }
            if (userRepository.existsByEmail(email)) {
                logger.warn("email {} already exists", email);
                response.put("status", false);
                response.put("message", "user with this email already exists");
                throw new UserAlreadyExistsException("user with email " + email + " already exists");
            }

            Set<Role> roleSet = new HashSet<>();
            Role role = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalArgumentException("role not found"));

            roleSet.add(role);

            ///  apply if this request asks for an Admin role
            if (userRegistration.getRoles() != null && userRegistration.getRoles().contains("ROLE_ADMIN")) {
                Role roles = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new IllegalArgumentException("role not found"));

                roleSet.add(roles);
            }

            User user = new User();
            user.setUsername(userRegistration.getUsername());
            user.setFirstName(userRegistration.getFirstName());
            user.setLastName(userRegistration.getLastName());
            user.setEmail(userRegistration.getEmail());
            user.setPassword(passwordEncoder.encode(userRegistration.getPassword()));
            user.setRiskLevel(RiskLevel.MEDIUM);
            user.setRoles(roleSet);
            userRepository.save(user);


            UserPrincipal userPrincipal = UserPrincipal.create(user);
            logger.info("registering user with username {} successful", userPrincipal.getUsername());
            response.put("user", userPrincipal);
            return response;


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.put("status", false);
            response.put("registration failed with message {}", e.getMessage());
            return response;

        }
    }
}
