package com.example.wasla.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a single field validation error.
 * Contains the field name, rejected value, and error message.
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class FieldError {
    private String field;
    private Object rejectedValue;
    private String message;
}
