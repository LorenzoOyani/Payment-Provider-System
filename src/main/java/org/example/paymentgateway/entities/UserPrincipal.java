package org.example.paymentgateway.entities;

import org.example.paymentgateway.enums.RiskLevel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String email;
    private final String password;
    private final String organizationId;
    private final RiskLevel riskLevel;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String email, String password, String organizationId, RiskLevel riskLevel, List<GrantedAuthority> authority) {
        this.id = id.intValue();
        this.email = email;
        this.password = password;
        this.organizationId = organizationId;
        this.riskLevel = riskLevel;
        this.authorities = authority;
    }

    /**
     * @param user  a user object that is wrapped by the UserPrincipal object DTO.
     * @return a UserPrincipal object.
     * */

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authority = user.getRoles().stream()
                .flatMap(role -> role.getPermission().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getOrganizationId(),
                user.getRiskLevel(),
                authority
        );


    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return riskLevel != RiskLevel.LOW;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}
