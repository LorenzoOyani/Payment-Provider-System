package org.example.paymentgateway.controller;

import org.example.paymentgateway.dto.*;
import org.example.paymentgateway.mapper.UserMapper;
import org.example.paymentgateway.security.JwtAuthToken;
import org.example.paymentgateway.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final JwtAuthToken jwtAuthToken;
    private final AuthService authService;
    private final UserMapper userMapper;

    @Autowired
    public AuthController(JwtAuthToken jwtAuthToken, AuthService authService, UserMapper userMapper) {
        this.jwtAuthToken = jwtAuthToken;
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest loginRequest) {

        try {
            if (loginRequest.username() == null || loginRequest.username().trim().isBlank()) {
                log.info("username cannot be null or blank");
                final var errorResponse = Map.of("error", "Username cannot be blank");
                return ResponseEntity.badRequest()
                        .body(CreateErrorResponse.createApiResponse(errorResponse, "Username is required"));
            }

            if (loginRequest.password() == null || loginRequest.password().trim().isBlank()) {
                log.info("password cannot be null or blank");
                final var errorResponse = Map.of("error", "password cannot be blank");
                return ResponseEntity.badRequest().body(CreateErrorResponse.createApiResponse(errorResponse, "error occurred"));
            }

            final String sanitizedUsername = loginRequest.username().trim().toLowerCase();

            final UserInfoResponse response = this.authService.authenticateUser(loginRequest);
            if (response == null) {
                final var errorResponse = Map.of("error", "Authentication failed");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(CreateErrorResponse.createApiResponse(errorResponse, "Authentication service unavailable"));
            }

            if (response.getJwtToken() == null || response.getJwtToken().trim().isEmpty()) {
                log.warn("Authentication failed - no token generated for username: {}", sanitizedUsername);
                final var errorResponse = Map.of("error", "Authentication failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CreateErrorResponse.createApiResponse(errorResponse, "Invalid credentials"));
            }

            log.info("user successfully authenticated");
            if (log.isDebugEnabled() && response.getJwtToken() != null) {
                jwtAuthToken.printTokenDiagnostics(response.getJwtToken());
            }

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warn("user credentials invalid for user: {}", loginRequest.username());
            final var mappedResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CreateErrorResponse.create(mappedResponse, "user failed to authenticate"));
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}, {}", loginRequest.username(), e.getMessage());
            final var mappedResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CreateErrorResponse.create(mappedResponse, "user failed to authenticate"));

        } catch (Exception e) {
            log.error("unexpected error encountered during Authentication: {}", e.getMessage());
            final var mappedResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CreateErrorResponse.create(mappedResponse, e.getMessage()));

        }


    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistration userRegistration) {

        try {
            if (userRegistration.getUsername() == null || userRegistration.getUsername().trim().isBlank()) {
                log.info("username cannot be null");
                final var errorResponse = Map.of("error", "username cannot be blank");
                return ResponseEntity.badRequest().body(CreateErrorResponse.createApiResponse(errorResponse, "user cannot be empty"));
            }
            if (userRegistration.getPassword() == null || userRegistration.getPassword().trim().isBlank() || !userRegistration.isPassWordMatch()) {
                log.info("password cannot be null");
                final var errorResponse = Map.of("error", "password does not match");
                return ResponseEntity.badRequest().body(CreateErrorResponse.createApiResponse(errorResponse, "password cannot be blank"));
            }

            if (!userRegistration.isPassWordMatch()) {
                log.info("password does not match");
                final var errorResponse = Map.of("error", "Password confirmation does not match");
                return ResponseEntity.badRequest().body(CreateErrorResponse.create(errorResponse, "Passwords must match"));
            }
            if (userRegistration.getEmail() != null && !isValidEmail(userRegistration.getEmail())) {

                log.warn("Registration attempt with invalid email format for username: {}", userRegistration.getUsername());
                final var errorResponse = Map.of("error", "Invalid email format");
                return ResponseEntity.badRequest().body(CreateErrorResponse.createApiResponse(errorResponse, "Please provide a valid email address"));
            }


            final Map<String, Object> response = this.authService.registerUser(userRegistration);


            if (response.isEmpty()) {
                log.warn("service validation failed for registration request");
                final var errorResponse = Map.of("error", "Invalid registration data");
                return ResponseEntity.badRequest()
                        .body(CreateErrorResponse.createApiResponse(errorResponse, "Registration data validation failed"));
            }

            if (response.containsKey("status") && Boolean.FALSE.equals(response.get("status"))) {
                String errorResponseMessage = (String) response.get("message");
                log.warn("Registration failed for user with username {}: {} ", userRegistration.getUsername(), errorResponseMessage);


                if (errorResponseMessage != null) {
                    if (errorResponseMessage.contains("already exists") || errorResponseMessage.contains("duplicate")) {
                        final var responseMessage = Map.of("error", errorResponseMessage);
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(CreateErrorResponse.createApiResponse(responseMessage, "failed registration request for already existed user"));
                    } else {
                        final var errorResponse = Map.of("error", errorResponseMessage != null ? errorResponseMessage : "registration failed");
                        return ResponseEntity.badRequest()
                                .body(CreateErrorResponse.createApiResponse(errorResponse, errorResponseMessage));
                    }
                }
            }

            if (response.containsKey("user")) {
                log.info("user successfully registered: {} ", userRegistration.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(response);
            }

            /// add this fallback for unexpected response format

            log.warn("Unexpected response format from registration service for username: {}", userRegistration.getUsername());
            final var errorResponse = Map.of("error", "Registration completed but response format unexpected");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CreateErrorResponse.createApiResponse(errorResponse, "Registration status unclear"));


        }catch (Exception e) {
            log.error("Unexpected error during user registration for username: {}", userRegistration.getUsername(), e);
            final var errorResponse = Map.of("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CreateErrorResponse.createApiResponse(errorResponse, "Registration temporarily unavailable"));
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}$");
    }

}
