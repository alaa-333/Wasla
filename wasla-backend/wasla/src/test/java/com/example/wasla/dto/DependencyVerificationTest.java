package com.example.wasla.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verification test to ensure all required dependencies are available.
 * This test validates that Lombok and Jakarta Bean Validation are properly configured.
 * Jackson is provided by spring-boot-starter-webmvc and will be tested in integration tests.
 */
class DependencyVerificationTest {

    @Test
    void lombokIsAvailable() {
        // Test Lombok @Data annotation
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        class TestDto {
            private String field;
        }

        TestDto dto = new TestDto("test");
        assertEquals("test", dto.getField());
        dto.setField("updated");
        assertEquals("updated", dto.getField());
        assertNotNull(dto.toString());
    }

    @Test
    void jakartaBeanValidationIsAvailable() {
        // Test Jakarta Bean Validation
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        assertNotNull(validator, "Jakarta Bean Validation should be available");
    }

    @Test
    void immutableLombokPatternWorks() {
        // Test Lombok @Getter and @AllArgsConstructor for immutable DTOs
        @Getter
        @AllArgsConstructor
        class ImmutableDto {
            private final String field;
        }

        ImmutableDto dto = new ImmutableDto("immutable");
        assertEquals("immutable", dto.getField());
    }
}
