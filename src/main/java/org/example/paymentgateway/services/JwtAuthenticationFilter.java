package org.example.paymentgateway.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtAuthToken jwtAuthToken;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtAuthToken jwtAuthToken, UserDetailsService userDetailsService) {
        this.jwtAuthToken = jwtAuthToken;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(AUTHORIZATION_HEADER);
        if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {

            final String token = this.extractTokenFromRequest(request);
            if (!ObjectUtils.isEmpty(token) && jwtAuthToken.validateToken(token)) {
                Authentication authentication = createAuthenticationRequest(token, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("successfully authenticated user {}", authentication.getName());
            }
        } catch (Exception e) {
            logger.error("Authentication failed with message, {}", e.getMessage());
            SecurityContextHolder.clearContext();
            return;
        }
        filterChain.doFilter(request, response);

    }

    private Authentication createAuthenticationRequest(String token, HttpServletRequest request) {
        DecodedJWT decodedJWT = jwtAuthToken.decodeToken(token);
        if (decodedJWT == null) {
            throw new RuntimeException("JWT decode failed");
        }

        final String username = extractUsername(decodedJWT);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;

    }


    private String extractUsername(DecodedJWT decodedJWT) {
        final String email = getClaimsAsStrings(decodedJWT);
        return StringUtils.hasText(email) ? email : decodedJWT.getSubject();
    }

    private String getClaimsAsStrings(DecodedJWT decodedJWT) {
        try {
            return decodedJWT.getClaim("email").asString();
        } catch (Exception e) {
            logger.info("claims string could not be parsed with message {} ", e.getMessage());
            return null;
        }

    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
