package com.example.wasla.common.exception;

public class UnauthorizedException extends WaslaAppException {
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode, errorCode.getStatus());
    }
}
