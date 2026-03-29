package com.example.wasla.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegisterRequest {

    @NotNull(message = "Full name is required")
    @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
    private String fullName;

    @NotNull(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "Phone is required")
    @Pattern(regexp = "^(\\+20|0)?(10|11|12|15)\\d{8}$",
            message = "Phone must be a valid Egyptian mobile number")
    private String phone;

    @NotNull(message = "License plate is required")
    @Size(min = 1, max = 20, message = "License plate must be between 1 and 20 characters")
    private String licensePlate;

    @NotNull(message = "Vehicle type is required")
    private String vehicleType;
}
