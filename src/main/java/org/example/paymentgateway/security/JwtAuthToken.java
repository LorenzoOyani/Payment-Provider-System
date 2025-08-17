package org.example.paymentgateway.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
            throw new IllegalStateException(String.format("the secret must be at least %d characters long for security with current length of %d",
                    MIN_SECRET_LENGTH, secret.length()));
        }

        if (expirationTime <= 0) {
            logger.info("expirationTime is negative");
            throw new IllegalStateException("expirationTime is negative");
        }
        logger.info("JWT configuration validated successfully");


    }

    private void initializeVerifier() {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        jwtVerifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();
        logger.info("JWT Verifier initialized successfully");
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
            String[] chunks = token.split("\\.");
            if(chunks.length != 3){
                logger.info("token validated failed");
                throw new JWTVerificationException("token validated failed with token format invalid");
            }
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
     * @return  JWT token subject or null
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

    /**
     * @param userDetails consist of the user information to process token generation
     * @return the token generated signed with JWT secret keys.
     * */

    public String generateToken(UserDetails userDetails) {
        if (userDetails == null || !StringUtils.hasText(userDetails.getUsername())) {
            logger.warn("cannot generate token: UserDetails is null or username is empty");
            return null;
        }
        try {
            Instant now = Instant.now();
            Instant expirations = now.plus(expirationTime, ChronoUnit.MILLIS);

            String token = JWT.create()
                    .withIssuer("auth0")
                    .withSubject(userDetails.getUsername())
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(expirations))
                    .withClaim("authorities", userDetails.getAuthorities().toString())
                    .sign(Algorithm.HMAC256(secret));

            logger.info("token generated successfully with token, {}", token);
            return token;

        } catch (Exception e) {
            logger.error("token generation failed for user: {} ", userDetails.getUsername(), e);
            return null;

        }
    }

    /**
     * @param jwtToken contains a validated token and length
     * logs standard JWT claims that are safe to expose.
     * */

    public void printTokenDiagnostics(String jwtToken) {
        try {

            if(jwtToken == null || jwtToken.trim().isEmpty() || isTokenExpired(jwtToken)) {
                logger.info("token is empty or null and token is expired");
                return;
            }

            String[] chunks = jwtToken.split("\\.");
            logger.info("total token segments: {}", chunks.length);
            if(chunks.length == 3){
                DecodedJWT jwt = JWT.decode(jwtToken);

                logger.info("Token diagnostics: {} issuer", jwt.getIssuer());
                logger.info("Token diagnostics: {} audience", this.getSubjectFromToken(jwt.getToken()));
                logger.info("Token diagnostics: {} expiresAt", jwt.getExpiresAt());
                logger.info("Token diagnostics: {} issuedAt", jwt.getIssuedAt());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
