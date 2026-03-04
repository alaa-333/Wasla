package com.example.wasla.repository;

import com.example.wasla.model.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
}
