package com.file.storage.api.dto.response;

import lombok.Data;

import java.time.Instant;

@Data
public class FileUploadResponseDto {

    private String id;
    private String fileName;
    private String mediaType;
    private long fileSize;
    private Instant createdAt;
}
