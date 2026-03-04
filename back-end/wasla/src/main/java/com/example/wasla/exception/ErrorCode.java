package com.example.wasla.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // ========================================
    // Validation Errors (VAL-XXX)
    // ========================================
    VALIDATION_FAILED("VAL-001", "Request validation failed", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("VAL-002", "Invalid input provided", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("VAL-003", "Required field is missing", HttpStatus.BAD_REQUEST),
    INVALID_FORMAT("VAL-004", "Invalid data format", HttpStatus.BAD_REQUEST),
    INVALID_OPERATION("VAL-005", "Invalid operation", HttpStatus.BAD_REQUEST),

    // ========================================
    // Authentication & Authorization Errors (AUTH-XXX)
    // ========================================
    UNAUTHORIZED_ACCESS("AUTH-001", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("AUTH-002", "Forbidden", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("AUTH-003", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("AUTH-004", "Authentication token expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("AUTH-005", "Invalid authentication token", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH-006", "Account is locked", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_PERMISSIONS("AUTH-007", "Insufficient permissions", HttpStatus.FORBIDDEN),

    // ========================================
    // System & Infrastructure Errors (SYS-XXX)
    // ========================================
    INTERNAL_SERVER_ERROR("SYS-001", "Unexpected server error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("SYS-002", "Invalid request data", HttpStatus.BAD_REQUEST),
    DATABASE_ERROR("SYS-003", "Database error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SYS-004", "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    REQUEST_TIMEOUT("SYS-005", "Request timeout", HttpStatus.REQUEST_TIMEOUT),
    DATA_INTEGRITY_VIOLATION("SYS-006", "Data integrity violation", HttpStatus.CONFLICT),
    EXTERNAL_SERVICE_ERROR("SYS-007", "External service unavailable", HttpStatus.SERVICE_UNAVAILABLE),

    // ========================================
    // User Entity Errors (USER-XXX)
    // ========================================
    USER_NOT_FOUND("USER-001", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER-002", "User already exists", HttpStatus.CONFLICT),
    USER_EMAIL_DUPLICATE("USER-003", "Email already registered", HttpStatus.CONFLICT),
    USER_USERNAME_DUPLICATE("USER-004", "Username already taken", HttpStatus.CONFLICT),
    USER_INVALID_EMAIL("USER-005", "Invalid email format", HttpStatus.BAD_REQUEST),
    USER_INVALID_PASSWORD("USER-006", "Invalid password format", HttpStatus.BAD_REQUEST),
    USER_INACTIVE("USER-007", "User account is inactive", HttpStatus.FORBIDDEN),
    USER_SUSPENDED("USER-008", "User account is suspended", HttpStatus.FORBIDDEN),
    USER_UPDATE_FAILED("USER-009", "Failed to update user", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_DELETE_FAILED("USER-010", "Failed to delete user", HttpStatus.INTERNAL_SERVER_ERROR),


    // ========================================
    // Notification Entity Errors (NOTIF-XXX)
    // ========================================
    NOTIFICATION_NOT_FOUND("NOTIF-001", "Notification not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_CREATION_FAILED("NOTIF-002", "Failed to create notification", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTIFICATION_UPDATE_FAILED("NOTIF-003", "Failed to update notification", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTIFICATION_DELETE_FAILED("NOTIF-004", "Failed to delete notification", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTIFICATION_UNAUTHORIZED("NOTIF-005", "Unauthorized to access this notification", HttpStatus.FORBIDDEN),
    NOTIFICATION_INVALID_TYPE("NOTIF-006", "Invalid notification type", HttpStatus.BAD_REQUEST);


    private final String code;
    private final String msg;
    private final HttpStatus status;

}
