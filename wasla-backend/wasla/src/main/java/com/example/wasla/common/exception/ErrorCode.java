package com.example.wasla.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Centralized error codes for the application.
 * Organized by domain module for easy identification.
 */
@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // ── Identity & Access (AUTH-XXX) ──
    UNAUTHORIZED_ACCESS("AUTH-001", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("AUTH-002", "Forbidden action", HttpStatus.FORBIDDEN),
    WRONG_CREDENTIALS("AUTH-003", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    EMAIL_ALREADY_EXISTS("AUTH-004", "Email address is already registered", HttpStatus.CONFLICT),
    INVALID_REFRESH_TOKEN("AUTH-005", "Invalid or expired refresh token", HttpStatus.UNAUTHORIZED),

    // ── User & Driver Management (USER-XXX) ──
    USER_NOT_FOUND("USER-001", "User not found in the system", HttpStatus.NOT_FOUND),
    DRIVER_UNAVAILABLE("USER-002", "Driver is currently offline or on another job", HttpStatus.CONFLICT),
    DRIVER_LOCATION_MISSING("USER-003", "Driver coordinates are not available", HttpStatus.BAD_REQUEST),
    DRIVER_PROFILE_NOT_FOUND("USER-004", "Driver profile not found", HttpStatus.NOT_FOUND),

    // ── Job & Delivery Cycle (JOB-XXX) ──
    JOB_NOT_FOUND("JOB-001", "Requested job could not be found", HttpStatus.NOT_FOUND),
    JOB_ALREADY_ASSIGNED("JOB-002", "Job is already assigned to a driver", HttpStatus.CONFLICT),
    JOB_STATUS_INVALID("JOB-003", "Invalid job status transition", HttpStatus.BAD_REQUEST),
    JOB_EXPIRED("JOB-004", "This job has expired", HttpStatus.GONE),
    JOB_NOT_OPEN("JOB-005", "Job is no longer accepting bids", HttpStatus.CONFLICT),

    // ── Bid (BID-XXX) ──
    BID_NOT_FOUND("BID-001", "Bid not found", HttpStatus.NOT_FOUND),
    BID_ALREADY_PLACED("BID-002", "You have already placed a bid on this job", HttpStatus.CONFLICT),
    BID_NOT_PENDING("BID-003", "Bid is no longer pending", HttpStatus.CONFLICT),
    DRIVER_NOT_ONLINE("BID-004", "Driver must be online to submit a bid", HttpStatus.BAD_REQUEST),

    // ── Rating (RAT-XXX) ──
    RATING_ALREADY_EXISTS("RAT-001", "A rating already exists for this job", HttpStatus.CONFLICT),
    RATING_JOB_NOT_COMPLETED("RAT-002", "Can only rate completed jobs", HttpStatus.BAD_REQUEST),
    RATING_NOT_JOB_OWNER("RAT-003", "Only the job owner can submit a rating", HttpStatus.FORBIDDEN),

    // ── Routing & Integration (ROUT-XXX) ──
    GEOCODING_FAILED("ROUT-001", "Failed to resolve coordinates", HttpStatus.SERVICE_UNAVAILABLE),
    ROUTE_NOT_FOUND("ROUT-002", "Could not calculate a valid route", HttpStatus.BAD_REQUEST),
    DISTANCE_CALCULATION_FAILED("ROUT-003", "Distance calculation service is unreachable", HttpStatus.SERVICE_UNAVAILABLE),

    // ── Location Tracking (LOC-XXX) ──
    LOCATION_NOT_FOUND("LOC-001", "Location not found", HttpStatus.NOT_FOUND),
    INVALID_COORDINATES("LOC-002", "Invalid GPS coordinates", HttpStatus.BAD_REQUEST),

    // ── Pricing (PRC-XXX) ──
    INVALID_PRICE_CALCULATION("PRC-001", "Failed to calculate pricing", HttpStatus.INTERNAL_SERVER_ERROR),

    // ── General (GEN-XXX) ──
    RESOURCE_NOT_FOUND("GEN-001", "Resource not found", HttpStatus.NOT_FOUND),

    // ── System (SYS-XXX) ──
    VALIDATION_FAILED("VAL-001", "Request validation failed", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("SYS-001", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
