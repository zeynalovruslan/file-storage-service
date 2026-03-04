package com.file.storage.api.service.impl;

import com.file.storage.api.entity.AuditEntity;
import com.file.storage.api.enums.AuditResultEnum;
import com.file.storage.api.enums.AuditStatusEnum;
import com.file.storage.api.repository.ApiKeyRepository;
import com.file.storage.api.repository.AuditRepository;
import com.file.storage.api.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    public void log(HttpServletRequest req,
                    Long apiKeyId, AuditResultEnum action,
                    AuditStatusEnum status, String fileId,
                    String message) {

        try {
            AuditEntity audit = new AuditEntity();

            if (apiKeyId != null) {
                audit.setApiKey(apiKeyRepository.getReferenceById(apiKeyId));
            }

            audit.setAction(action);
            audit.setStatus(status);
            audit.setFileId(fileId);
            audit.setMessage(message);
            audit.setIp(req.getRemoteAddr());
            audit.setUserAgent(req.getHeader("User-Agent"));
            auditRepository.save(audit);
        } catch (Exception ignore) {

        }
    }

}

