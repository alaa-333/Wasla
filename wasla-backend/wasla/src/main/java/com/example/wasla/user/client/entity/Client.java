package com.example.wasla.user.client.entity;

import com.example.wasla.common.entity.BaseEntity;
import com.example.wasla.common.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Client entity — a person who posts move requests.
 * Contains authentication and profile information.
 */
@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Client extends BaseEntity implements User {

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;
}
