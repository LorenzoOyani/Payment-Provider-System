package org.example.paymentgateway.dto;

import org.example.paymentgateway.entities.Role;

import java.util.List;

public class UserInfoResponse {

    private final Long id;
    private final String jwtToken;
    private final String username;
    private final List<String> roles;

    ///  only builders can create instances
   private UserInfoResponse(Builder builder) {
        this.id = builder.id;
        this.jwtToken = builder.jwtToken;
        this.username = builder.username;
        this.roles = builder.roles;
    }

    public static class Builder {
        private Long id;
        private String jwtToken;
        private String username;
        private List<String> roles;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder jwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }
        public UserInfoResponse build() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("username cannot be null or empty");
            }
            if (jwtToken == null || jwtToken.trim().isEmpty()) {
                throw new IllegalArgumentException("jwtToken cannot be null or empty");
            }

            return new UserInfoResponse(this);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }


    public String getJwtToken() {
        return jwtToken;
    }


    public String getUsername() {
        return username;
    }


    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "UserInfoResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", jwtToken='" + (jwtToken != null ? "[PROTECTED]" : null) + '\'' +
                ", roles=" + roles +
                '}';


    }
}
