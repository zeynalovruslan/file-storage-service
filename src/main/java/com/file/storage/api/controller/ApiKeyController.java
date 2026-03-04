package com.file.storage.api.controller;

import com.file.storage.api.dto.response.ApiKeyResponseDto;
import com.file.storage.api.enums.AuditResultEnum;
import com.file.storage.api.enums.AuditStatusEnum;
import com.file.storage.api.service.ApiKeyService;
import com.file.storage.api.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api-keys")
public class ApiKeyController {
    private static final String HEADER_KEY = "SECRET-KEY";

    private final ApiKeyService apiKeyService;
    private final AuditService auditService;

    @PostMapping
    public ResponseEntity<ApiKeyResponseDto> createKey(@RequestHeader(value = HEADER_KEY, required = true)
                                                       @NotBlank String secretKey,
                                                       @RequestBody String name,
                                                       HttpServletRequest request) {
        try {
            ApiKeyResponseDto dto = apiKeyService.createKey(secretKey, name);

            auditService.log(request, null,
                    AuditResultEnum.CREATE_KEY, AuditStatusEnum.SUCCESS,
                    null, "name=" + name);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            auditService.log(request, null,
                    AuditResultEnum.CREATE_KEY, AuditStatusEnum.FAIL,
                    null, e.getMessage());
            throw e;
        }
    }

}
