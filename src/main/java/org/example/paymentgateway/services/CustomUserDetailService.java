package org.example.paymentgateway.services;

import org.example.paymentgateway.entities.User;
import org.example.paymentgateway.entities.UserPrincipal;
import org.example.paymentgateway.exception.UserAlreadyExistsException;
import org.example.paymentgateway.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final Logger log = LoggerFactory.getLogger(CustomUserDetailService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("reading user from database with username, {}", username);
        final var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with %s not found in database ".formatted(username)));

        return UserPrincipal.create(user); /// a wrapper class for a user object.
    }

    /// / todo- extract this method to a user service class for single responsibility purposes
    /**
     * @param user the user entity to save
     * @return user - the saved entity user object
     * @throws UserAlreadyExistsException if username and email already exist in this database
     */

    public User save(User user) {
        log.info("saving user {}", user);

        if (existByUsername(user.getUsername())) {
            log.info("user {} already exists", user.getUsername());
            throw new UserAlreadyExistsException("user with user name " + user.getUsername() + " already exists");
        }

        if (existByEmail(user.getEmail())) {
            log.info("user {} already exists", user.getEmail());
            throw new UserAlreadyExistsException("user with user email " + user.getEmail() + " already exists");
        }

        if (user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        log.info("saved!");
        return userRepository.save(user);

    }

    private boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }


}
