package org.example.paymentgateway.services;

import org.example.paymentgateway.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with %s not found in database ".formatted(username)));

        return UserPrincipal.create(user);
    }
}
