package com.file.storage.api.controller;

import com.file.storage.api.dto.response.FileDownloadResponseDto;
import com.file.storage.api.dto.response.FileResponseDto;
import com.file.storage.api.dto.response.FileUploadResponseDto;
import com.file.storage.api.enums.AuditResultEnum;
import com.file.storage.api.enums.AuditStatusEnum;
import com.file.storage.api.service.AuditService;
import com.file.storage.api.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class FileController {

    private static final String SUCCESS_MESSAGE = "The audit was successfully created";

    private final FileService fileService;
    private final AuditService auditService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileUploadResponseDto upload(@RequestPart("file") MultipartFile file,
                                        @RequestAttribute("apiKeyId") Long apiKeyId,
                                        HttpServletRequest request) {
        try {
            FileUploadResponseDto dto = fileService.upload(file, apiKeyId);

            auditService.log(request, apiKeyId,
                    AuditResultEnum.UPLOAD, AuditStatusEnum.SUCCESS,
                    dto.getId(), SUCCESS_MESSAGE);

            return dto;
        } catch (Exception e) {
            auditService.log(request, apiKeyId,
                    AuditResultEnum.AUTH_FAIL, AuditStatusEnum.FAIL,
                    null, e.getMessage());
            throw e;
        }


    }

    @GetMapping
    public Page<FileResponseDto> list(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return fileService.getFileList(pageable);
    }


    @GetMapping("/{id}")
    public void download(@PathVariable String id,
                         @RequestAttribute("apiKeyId") Long apiKeyId,
                         HttpServletResponse response,
                         HttpServletRequest request) {

        FileDownloadResponseDto fileResponse = fileService.download(id, apiKeyId);

        response.setContentType(fileResponse.getContentType());
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileResponse.getOriginalName() + "\""
        );
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileResponse.getSizeBytes()));

        try (InputStream stream = fileResponse.getStream()) {
            StreamUtils.copy(stream, response.getOutputStream());

            auditService.log(request, apiKeyId,
                    AuditResultEnum.DOWNLOAD, AuditStatusEnum.SUCCESS,
                    id, SUCCESS_MESSAGE);

        } catch (Exception e) {
            auditService.log(request, apiKeyId,
                    AuditResultEnum.DOWNLOAD, AuditStatusEnum.FAIL,
                    id, e.getMessage());

            throw new RuntimeException("Streaming failed", e);
        }
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id,
                       @RequestAttribute("apiKeyId") Long apiKeyId,
                       HttpServletRequest request) {
        try {
            fileService.delete(id, apiKeyId);

            auditService.log(request, apiKeyId,
                    AuditResultEnum.DELETE, AuditStatusEnum.SUCCESS,
                    id, SUCCESS_MESSAGE);

        } catch (Exception e) {
            auditService.log(request, apiKeyId,
                    AuditResultEnum.DELETE, AuditStatusEnum.FAIL,
                    id, e.getMessage());
            throw e;
        }
    }


}
