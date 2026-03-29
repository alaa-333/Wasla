package com.example.wasla.user.driver.repository;

import com.example.wasla.user.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    Optional<Driver> findByEmail(String email);

    boolean existsByEmail(String email);
}
