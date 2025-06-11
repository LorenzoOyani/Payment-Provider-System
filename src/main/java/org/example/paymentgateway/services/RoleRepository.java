package org.example.paymentgateway.services;

import org.example.paymentgateway.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @NotNull
    @Override
    @Query(value = "SELECT u from Role u where u.id=:id")
    Optional<Role> findById(@NotNull Long id);
}
