package com.example.wasla.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class BusinessException extends RuntimeException{

    private HttpStatus status;
    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.getStatus());
    }

    public BusinessException(ErrorCode errorCode, HttpStatus status) {
        super(errorCode.getMsg());
        this.status = status;
    }
}
