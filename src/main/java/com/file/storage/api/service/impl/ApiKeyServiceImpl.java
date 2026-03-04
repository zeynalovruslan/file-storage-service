package com.file.storage.api.service.impl;

import com.file.storage.api.dto.response.ApiKeyResponseDto;
import com.file.storage.api.entity.ApiKeyEntity;
import com.file.storage.api.exception.BadRequestException;
import com.file.storage.api.exception.ForbiddenException;
import com.file.storage.api.repository.ApiKeyRepository;
import com.file.storage.api.service.ApiKeyService;
import com.file.storage.api.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final BCryptPasswordEncoder encoder;
    private final ApiKeyRepository apiKeyRepository;

    @Value("${security.admin-api-key}")
    private String adminKey;

    @Override
    public ApiKeyResponseDto createKey(String apiKey, String name) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new BadRequestException("Apikey can not be empty");
        }

        if (!apiKey.equals(adminKey)) {
            throw new ForbiddenException("Apikey does not match admin key");
        }

        String realKey = TokenUtil.generateToken();
        String encodedKey = encoder.encode(realKey);
        String prefix = TokenUtil.extractPrefix(realKey);

        ApiKeyEntity apiKeyEntity = new ApiKeyEntity();
        apiKeyEntity.setName(name);
        apiKeyEntity.setKeyHash(encodedKey);
        apiKeyEntity.setKeyPrefix(prefix);
        ApiKeyEntity savedEntity = apiKeyRepository.save(apiKeyEntity);

        ApiKeyResponseDto response = new ApiKeyResponseDto();
        response.setId(savedEntity.getId());
        response.setName(name);
        response.setApiKey(realKey);
        response.setPrefix(savedEntity.getKeyPrefix());

        return response;
    }

}
