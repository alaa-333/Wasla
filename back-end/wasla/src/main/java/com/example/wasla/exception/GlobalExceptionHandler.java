package com.example.wasla.exception;

import com.example.wasla.dto.response.FieldError;
import com.example.wasla.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for validation errors.
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions thrown when @Valid annotation fails.
     * 
     * @param ex the MethodArgumentNotValidException containing validation errors
     * @return ValidationErrorResponse with formatted field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.error("MethodArgumentNotValidException : {}",ex.getMessage());

        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .httpStatus(ex.getStatusCode().value())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .errors(fieldErrors)
                .build();
    }



    /**
     * Handles Business Exception
     */
    @ExceptionHandler(BusinessException.class)
    public ErrorResponse handleBusinessException(BusinessException ex, HttpServletRequest request) {

        log.error("Business exception: {}", ex.getMessage(), ex);

        var errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode().toString())
                .httpStatus(ex.getStatus().value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return errorResponse;
    }


    /**
     * Handles all other exceptions - returns HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex , HttpServletRequest request) {
        log.error("Unexpected exception occurred", ex);

        ErrorResponse response = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }
}
