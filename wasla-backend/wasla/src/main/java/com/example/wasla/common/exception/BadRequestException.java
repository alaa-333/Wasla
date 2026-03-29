package com.example.wasla.common.exception;

public class BadRequestException extends WaslaAppException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode, errorCode.getStatus());
    }
}
