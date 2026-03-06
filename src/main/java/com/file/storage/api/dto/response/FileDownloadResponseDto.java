package com.file.storage.api.dto.response;

import lombok.Data;

import java.io.InputStream;

@Data
public class FileDownloadResponseDto {
    private String fileName;
    private String mediaType;
    private long fileSize;
    private InputStream stream;
}
