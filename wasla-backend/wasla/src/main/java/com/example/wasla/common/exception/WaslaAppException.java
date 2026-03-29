package com.example.wasla.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base application exception. All domain exceptions extend this.
 */
@Getter
public class WaslaAppException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;

    public WaslaAppException(ErrorCode errorCode) {
        this(errorCode, errorCode.getStatus());
    }

    public WaslaAppException(ErrorCode errorCode, HttpStatus status) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = status;
    }

    public WaslaAppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.status = errorCode.getStatus();
    }
}
