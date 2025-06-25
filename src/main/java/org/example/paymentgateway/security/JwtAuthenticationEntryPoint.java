package org.example.paymentgateway.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
      AuthenticationException authenticationException = (AuthenticationException)  request.getAttribute("exception");
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
      response.setContentType("application/json");

      String message = authenticationException.getMessage() != null ? authenticationException.getMessage() : "Unauthorized";
      response.getWriter().write("{\"message\":\"" + message + "\"}");
      response.getWriter().flush();
    }
}
