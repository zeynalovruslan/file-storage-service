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

    @Value("${security.secret-api-key}")
    private String secretKey;

    @Override
    public ApiKeyResponseDto createKey(String reqSecretKey, String name) {

        if (reqSecretKey == null || reqSecretKey.isBlank()) {
            throw new BadRequestException("Secret key can not be empty");
        }

        if (!reqSecretKey.equals(secretKey)) {
            throw new ForbiddenException("The secret key you sent is incorrect.");
        }

        boolean exists = apiKeyRepository.existsByName(name);
        if (exists) {
            throw new BadRequestException("Apikey already exists");
        }

        String realKey = TokenUtil.generateToken();
        String encodedKey = encoder.encode(realKey);
        String shortKey = TokenUtil.extractPrefix(realKey);

        ApiKeyEntity apiKeyEntity = new ApiKeyEntity();
        apiKeyEntity.setName(name);
        apiKeyEntity.setApiKeyHash(encodedKey);
        apiKeyEntity.setShortKey(shortKey);
        ApiKeyEntity savedEntity = apiKeyRepository.save(apiKeyEntity);

        ApiKeyResponseDto response = new ApiKeyResponseDto();
        response.setId(savedEntity.getId());
        response.setName(name);
        response.setApiKey(realKey);
        response.setShortKey(savedEntity.getShortKey());

        return response;
    }

}
