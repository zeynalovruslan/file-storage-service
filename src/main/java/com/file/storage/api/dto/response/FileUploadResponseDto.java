package com.file.storage.api.dto.response;

import lombok.Data;

import java.time.Instant;

@Data
public class FileUploadResponseDto {

    private String id;
    private String originalName;
    private String contentType;
    private long sizeBytes;
    private Instant createdAt;
}
