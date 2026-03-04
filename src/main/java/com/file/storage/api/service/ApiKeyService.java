package com.file.storage.api.service;

import com.file.storage.api.dto.response.ApiKeyResponseDto;

public interface ApiKeyService {
    ApiKeyResponseDto createKey(String apiKey, String name);

}
