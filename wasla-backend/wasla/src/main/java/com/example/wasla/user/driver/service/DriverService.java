package com.example.wasla.user.driver.service;

import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.user.driver.entity.Driver;
import com.example.wasla.user.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Driver profile management service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    @Transactional(readOnly = true)
    public Driver findById(UUID driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public Driver updateAvailability(UUID driverId, boolean isAvailable) {
        Driver driver = findById(driverId);
        driver.setIsAvailable(isAvailable);
        Driver saved = driverRepository.save(driver);
        log.info("Driver {} availability updated: {}", driverId, isAvailable);
        return saved;
    }

    @Transactional
    public Driver updateLocation(UUID driverId, BigDecimal lat, BigDecimal lng) {
        Driver driver = findById(driverId);
        driver.setCurrentLat(lat);
        driver.setCurrentLng(lng);
        Driver saved = driverRepository.save(driver);
        log.debug("Driver {} location updated: ({}, {})", driverId, lat, lng);
        return saved;
    }

    @Transactional
    public Driver updateProfile(UUID driverId, String fullName, String phone, String photoUrl) {
        Driver driver = findById(driverId);
        
        if (fullName != null) {
            driver.setFullName(fullName);
        }
        if (phone != null) {
            driver.setPhone(phone);
        }
        if (photoUrl != null) {
            driver.setPhotoUrl(photoUrl);
        }
        
        Driver saved = driverRepository.save(driver);
        log.info("Driver {} profile updated", driverId);
        return saved;
    }

    @Transactional
    public void updateFcmToken(UUID driverId, String fcmToken) {
        Driver driver = findById(driverId);
        driver.setFcmToken(fcmToken);
        driverRepository.save(driver);
        log.info("Driver {} FCM token updated", driverId);
    }
}
