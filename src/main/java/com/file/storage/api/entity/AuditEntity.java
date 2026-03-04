package com.file.storage.api.entity;

import com.file.storage.api.enums.AuditResultEnum;
import com.file.storage.api.enums.AuditStatusEnum;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_created_at", columnList = "created_at"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_file_id", columnList = "file_id"),
        @Index(name = "idx_audit_api_key", columnList = "api_key_id")
})
public class AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="api_key_id", foreignKey = @ForeignKey(name="fk_audit_api_key"))
    private ApiKeyEntity apiKey;

    @Enumerated(EnumType.STRING)
    @Column(name="action", nullable = false, length = 50)
    private AuditResultEnum action;

    @Column(name="file_id", length = 45)
    private String fileId;

    @Column(name="ip", length = 45)
    private String ip;

    @Column(name="user_agent", length = 512)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 20)
    private AuditStatusEnum status;

    @Lob
    @Column(name="message")
    private String message;

    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

}
