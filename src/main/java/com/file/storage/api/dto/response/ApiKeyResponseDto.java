package com.file.storage.api.dto.response;

import lombok.Data;

@Data
public class ApiKeyResponseDto {

    private Long id;
    private String name;
    private String apiKey;
    private String prefix;
}
