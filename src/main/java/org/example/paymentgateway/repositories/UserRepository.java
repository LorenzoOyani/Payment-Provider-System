package org.example.paymentgateway.repositories;

import org.example.paymentgateway.entities.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

     String USER_CACHE_KEY = "users";

    @Query(value = "SELECT u FROM User u WHERE u.email=:email ")
    Optional<User> findByEmail(String email);

    @Cacheable(value =USER_CACHE_KEY )
    Optional<User> findByUsername(String username);

    @CacheEvict(value =USER_CACHE_KEY)
    <S extends User> S save(S entity);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
