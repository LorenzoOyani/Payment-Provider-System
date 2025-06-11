package org.example.paymentgateway.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;


@Component
public class JwtAuthToken {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthToken.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private JWTVerifier jwtVerifier;

    /**
     * 32 - 256 bits
     */

    private static final int MIN_SECRET_LENGTH = 32;

    @PostConstruct
    public void init() {
        validateConfiguration();
        initializeVerifier();
    }


    private void validateConfiguration() {
        if (!StringUtils.hasText(secret)) {
            logger.info("secret is empty");
            throw new JWTVerificationException("secret is empty");
        }

        if (secret.length() < MIN_SECRET_LENGTH) {
            logger.info("secret is too short");
            throw new IllegalStateException(String.format("the secrete must be at least %d characters long for security with current length of %d",
                    MIN_SECRET_LENGTH, secret.length()));
        }

        if (expirationTime < 0) {
            logger.info("expirationTime is negative");
            throw new IllegalStateException("expirationTime is negative");
        }
        logger.info("JWT configuration validated successfully");


    }

    private void initializeVerifier() {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        jwtVerifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .acceptLeeway(expirationTime)
                .build();
    }


    /**
     * @param token JWT token to validate
     * @return true if token is validated and false if otherwise.
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            logger.info("token is empty");
            throw new JWTVerificationException("token is empty OR null");
        }
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().before(new Date())) {
                logger.info("token validation failed: token is expired");
                throw new JWTVerificationException("token is expired");
            }
            logger.info("token validated successfully for subject, {}", jwt.getSubject());
            return true;
        } catch (JWTVerificationException e) {
            logger.warn("JWT verification failed with message {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.warn("token verification failed with message {}", e.getMessage());
            return false;
        }
    }

    /**
     * @param token JWT token
     * @return subject or null
     */
    public String getSubjectFromToken(String token) {
        if (validateToken(token)) {
            try {
                DecodedJWT jwt = jwtVerifier.verify(token);
                return jwt.getSubject();
            } catch (JWTVerificationException e) {
                logger.warn("failed to extract subject from token: {}", e.getMessage());
            }
        }
        return null;
    }

    public boolean isTokenExpired(String token) {
        if (!StringUtils.hasText(token)) {
            logger.info("token is empty or null");
            throw new JWTVerificationException("token is empty");
        }
        DecodedJWT jwt = decodeToken(token);
        if (jwt == null || jwt.getExpiresAt() == null) {
            logger.info("token is expired");
            throw new JWTVerificationException("token is expired, use a refreshed token");
        }
        return jwt.getExpiresAt().before(new Date());
    }

    /**
     * @param token JWT token
     * @return The decoded token or null
     **/

   public DecodedJWT decodeToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            return JWT.decode(token);
        } catch (JWTVerificationException e) {
            logger.warn("failed to decode {}, with error message of, {}", token, e.getMessage());
            return null;
        }
    }


}
