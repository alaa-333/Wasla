package com.example.wasla.user.client.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfileDto {
    private String id;
    private String fullName;
    private String email;
    private String phone;
}
