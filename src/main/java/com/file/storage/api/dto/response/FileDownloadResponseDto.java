package com.file.storage.api.dto.response;

import lombok.Data;

import java.io.InputStream;

@Data
public class FileDownloadResponseDto {
    private String originalName;
    private String contentType;
    private long sizeBytes;
    private InputStream stream;
}
