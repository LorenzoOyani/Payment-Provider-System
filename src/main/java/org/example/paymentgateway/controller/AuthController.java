package org.example.paymentgateway.controller;

import org.example.paymentgateway.dto.*;
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

    @Autowired
    public AuthController(JwtAuthToken jwtAuthToken, AuthService authService) {
        this.jwtAuthToken = jwtAuthToken;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest loginRequest) {

        try {
            if (loginRequest.username() == null || loginRequest.username().trim().isBlank()) {
                log.info("username cannot be null or blank");
                return ResponseEntity.badRequest().body(new CreateErrorResponse<>("user cannot be empty"));
            }

            if (loginRequest.password() == null || loginRequest.password().trim().isBlank()) {
                log.info("password cannot be null or blank");
                return ResponseEntity.badRequest().body(new CreateErrorResponse<>("password cannot be empty"));
            }

            final UserInfoResponse response = this.authService.authenticateUser(loginRequest);
            log.info("user successfully authenticated");
            if (response.getJwtToken() != null) {
                jwtAuthToken.printTokenDiagnostics(response.getJwtToken());
            }

            return ResponseEntity.ok(response);
        }catch (BadCredentialsException e){
            log.warn("user credentials invalid for user: {}", loginRequest.username());
            final var mappedResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CreateErrorResponse.create(mappedResponse, "user failed to authenticate"));
        }catch (AuthenticationException e){
            log.error("Authentication failed for user: {}, {}", loginRequest.username(), e.getMessage());
            final var mappedResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CreateErrorResponse.create(mappedResponse, "user failed to authenticate"));

        }catch (Exception e){
            log.error("unexpected error encountered during Authentication: {}", e.getMessage());
            final var mappedResponse = Map.of("error", e.getMessage());

        }


    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistration userRegistration){
        if(userRegistration.getUsername() == null || userRegistration.getUsername().trim().isBlank()){
            log.info("username cannot be null");
            final var errorResponse = Map.of("error", "username cannot be blank");
            return ResponseEntity.badRequest().body(CreateErrorResponse.create(errorResponse, "user cannot be empty"));
        }
        if(userRegistration.getPassword() == null || userRegistration.getPassword().trim().isBlank() || !userRegistration.isPassWordMatch()){
            log.info("password cannot be null");
            final var errorResponse = Map.of("error", "password does not match");
            return ResponseEntity.badRequest().body(CreateErrorResponse.create(errorResponse, "password cannot be blank"));
        }

        final Map<String, Object> response = this.authService.registerUser(userRegistration);



    }

}
