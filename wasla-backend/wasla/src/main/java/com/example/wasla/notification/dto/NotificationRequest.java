package com.example.wasla.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NotificationRequest {

    private String fcmToken;
    private String title;
    private String body;

   // optional data payload (key-value pairs)
    private Map<String, String>  data;
}
