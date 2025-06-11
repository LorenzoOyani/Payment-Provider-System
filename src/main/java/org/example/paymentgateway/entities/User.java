package org.example.paymentgateway.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//@Getter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private String email;

    @Basic(optional = false)
    private String firstName;

    @Basic(optional = false)
    private String lastName;


    private String password;

    @Basic(optional = false)
    private UserStatus userStatus = UserStatus.ACTIVE;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_user",
            joinColumns = @JoinColumn(
                    name = "user_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id"
            )
    )
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel = RiskLevel.LOW;

    private String organizationId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        ///  every role has a corresponding permissions.
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            for (Permission permission : role.getPermission()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getId() {
        return id;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }

    public boolean hasPermission(String permissionName) {
        return roles.stream().
                flatMap(role -> role.getPermission().stream())
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }

    public boolean hasAnyPermission(String... permissions) {
        return Arrays.stream(permissions)
                .anyMatch(this::hasPermission);
    }

    public Set<String> getRoleNames() {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    public Set<String> getPermissionNames() {
        return roles.stream().flatMap(role -> role.getPermission().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}
