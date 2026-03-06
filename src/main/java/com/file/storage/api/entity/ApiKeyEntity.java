package com.file.storage.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "api_keys", indexes = {
        @Index(name = "idx_api_keys_active", columnList = "is_active"),
        @Index(name = "idx_api_keys_created_at", columnList = "created_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_api_keys_api_key_hash", columnNames = "api_key_hash")
})
public class ApiKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "api_key_hash", nullable = false, length = 255)
    private String apiKeyHash;

    @Column(name = "short_key", length = 20)
    private String shortKey;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();


    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
