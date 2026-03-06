package com.file.storage.api.service;

import com.file.storage.api.enums.AuditResultEnum;
import com.file.storage.api.enums.AuditStatusEnum;
import jakarta.servlet.http.HttpServletRequest;

public interface AuditService {

    void log(HttpServletRequest req,
             Long clientKey,
             AuditResultEnum result,
             AuditStatusEnum status,
             String fileId,
             String message);
}
