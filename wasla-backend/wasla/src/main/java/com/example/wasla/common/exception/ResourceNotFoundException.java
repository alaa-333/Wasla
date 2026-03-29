package com.example.wasla.common.exception;

public class ResourceNotFoundException extends WaslaAppException {
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode, errorCode.getStatus());
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
              String.format("%s not found with %s: %s", resource, field, value));
    }
}

