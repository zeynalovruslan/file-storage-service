package com.file.storage.api.filter;


import com.file.storage.api.entity.ApiKeyEntity;
import com.file.storage.api.enums.AuditResultEnum;
import com.file.storage.api.enums.AuditStatusEnum;
import com.file.storage.api.repository.ApiKeyRepository;
import com.file.storage.api.service.AuditService;
import com.file.storage.api.util.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final int PREFIX_LEN = 10;

    private final BCryptPasswordEncoder encoder;
    private final ApiKeyRepository apiKeyRepository;
    private final AuditService auditService;

    @Value("${security.secret-api-key}")
    private String adminKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isAdminEndpoint = path.startsWith("/api-keys");

        String key = request.getHeader("X-API-Key");
        if (key == null || key.isBlank()) {
            unauthorized(request, response, "missing X-API-Key, path=" + path);
            return;
        }

        if (isAdminEndpoint) {
            if (adminKey != null && !adminKey.isBlank() && adminKey.equals(key)) {
                filterChain.doFilter(request, response);
                return;
            }
            forbidden(request, response, "admin key required, path=" + path);
            return;
        }

        String prefix = TokenUtil.extractPrefix(key);
        if (prefix == null) {
            unauthorized(request, response, "invalid token format (prefix), path=" + path);
            return;
        }

        ApiKeyEntity entity = apiKeyRepository.findByKeyPrefixAndActiveTrue(prefix).orElse(null);
        if (entity == null) {
            unauthorized(request, response, "invalid token format (prefix), path=" + path);
            return;
        }

        if (!encoder.matches(key, entity.getKeyHash())) {
            unauthorized(request, response, "api key not found, path=" + path);
            return;
        }

        request.setAttribute("apiKeyId", entity.getId());
        filterChain.doFilter(request, response);
    }


    private void unauthorized(HttpServletRequest request,
                              HttpServletResponse response,
                              String message) throws IOException {
        try {
            auditService.log(request, null, AuditResultEnum.AUTH_FAIL, AuditStatusEnum.FAIL, null, message);
        } catch (Exception ignore) {
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Unauthorized\"}");
    }

    private void forbidden(HttpServletRequest request,
                           HttpServletResponse response,
                           String message) throws IOException {
        try {
            auditService.log(request, null, AuditResultEnum.AUTH_FAIL, AuditStatusEnum.FAIL, null, message);
        } catch (Exception ignore) {
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Forbidden\"}");
    }


}
