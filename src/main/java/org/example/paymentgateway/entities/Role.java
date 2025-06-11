package org.example.paymentgateway.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    public Role(String name){
        this.name = name;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permission = new HashSet<>();


    @Column(name = "organization_id")
    private Long organizationId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Role() {

    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Permission> getPermission() {
        return permission;
    }
}
