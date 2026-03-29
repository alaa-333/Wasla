package com.example.wasla.auth.security;

import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.UnauthorizedException;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.user.client.entity.Client;
import com.example.wasla.user.client.repository.ClientRepository;
import com.example.wasla.user.driver.entity.Driver;
import com.example.wasla.user.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Helper to extract authenticated Client or Driver from SecurityContext.
 */
@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final ClientRepository clientRepository;
    private final DriverRepository driverRepository;

    /**
     * Get the currently authenticated client.
     * Throws exception if not authenticated or not a client.
     */
    public Client getCurrentClient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        String email = auth.getName();
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Get the currently authenticated driver.
     * Throws exception if not authenticated or not a driver.
     */
    public Driver getCurrentDriver() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        String email = auth.getName();
        return driverRepository.findByEmail(email)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Get the current user ID (works for both client and driver).
     */
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        String email = auth.getName();
        
        // Try client first
        return clientRepository.findByEmail(email)
                .map(Client::getId)
                .orElseGet(() -> driverRepository.findByEmail(email)
                        .map(Driver::getId)
                        .orElseThrow(() -> new WaslaAppException(ErrorCode.USER_NOT_FOUND)));
    }

    /**
     * Get the current user's role.
     */
    public String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACCESS));
    }

    /**
     * Check if current user is a client.
     */
    public boolean isClient() {
        return "CLIENT".equals(getCurrentUserRole());
    }

    /**
     * Check if current user is a driver.
     */
    public boolean isDriver() {
        return "DRIVER".equals(getCurrentUserRole());
    }
}
