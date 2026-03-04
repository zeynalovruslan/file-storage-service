package com.file.storage.api.entity;

import com.file.storage.api.enums.ProviderEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "files", indexes = {
        @Index(name = "idx_files_created_at", columnList = "created_at"),
        @Index(name = "idx_files_deleted_at", columnList = "deleted_at"),
        @Index(name = "idx_files_created_by", columnList = "created_by_api_key_id")
})
public class FileEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "original_name", nullable = false, length = 512)
    private String originalName;

    @Column(name = "content_type", length = 255)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider", nullable = false, length = 30)
    private ProviderEnum storageProvider;

    @Column(name = "bucket", nullable = false, length = 255)
    private String bucket;

    @Column(name = "object_key", nullable = false, length = 1000)
    private String objectKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_api_key_id",
            foreignKey = @ForeignKey(name = "fk_files_api_key"))
    private ApiKeyEntity createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;


    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = Instant.now();
        if (storageProvider == null) storageProvider = ProviderEnum.MINIO;
    }
}
