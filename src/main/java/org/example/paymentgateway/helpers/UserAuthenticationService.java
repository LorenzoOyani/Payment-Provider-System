package org.example.paymentgateway.helpers;


import org.example.paymentgateway.entities.User;
import org.example.paymentgateway.entities.UserPrincipal;
import org.example.paymentgateway.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService {
    private final Logger log = LoggerFactory.getLogger(UserAuthenticationService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserAuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get the currently authenticated user in the security context
     *
     * @return an authenticated user entity
     * @throws org.springframework.security.core.AuthenticationException               for failed authenticated user
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException for invalid user in security context
     */

    public User getCurrentAuthenticatedUser() {

        final Authentication authentication = getAuthenticatedUser();

        if (!isValidAuthenticatedUser(authentication)) {
            log.warn("Authentication failed for user {}", authentication.getName());
            throw new org.example.paymentgateway.exception.AuthenticationException("Authentication failed for user");
        }

        UserPrincipal userPrincipal = extractUserPrincipalFromAuthentication(authentication);

        String username = userPrincipal.getUsername();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {

                    log.warn("User {} not found", username);
                    return new UsernameNotFoundException("user " + username + " not found");
                });

    }

    private UserPrincipal extractUserPrincipalFromAuthentication(final Authentication authentication) {
        Object userPrincipal = authentication.getPrincipal();

        if (!(userPrincipal instanceof UserPrincipal)) {
            log.warn("expected {} but got {}", UserPrincipal.class.getName(), userPrincipal.getClass().getName());
            throw new org.example.paymentgateway.exception.AuthenticationException("Invalid user principal");
        }
        return (UserPrincipal) userPrincipal;
    }


    private boolean isValidAuthenticatedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserPrincipal
                && !"anonymousUser".equals(authentication.getName());
    }

    private Authentication getAuthenticatedUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}