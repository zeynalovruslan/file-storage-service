package com.file.storage.api.service.impl;

import com.file.storage.api.dto.response.FileDownloadResponseDto;
import com.file.storage.api.dto.response.FileResponseDto;
import com.file.storage.api.dto.response.FileUploadResponseDto;
import com.file.storage.api.entity.ApiKeyEntity;
import com.file.storage.api.entity.FileEntity;
import com.file.storage.api.enums.ProviderEnum;
import com.file.storage.api.exception.BadRequestException;
import com.file.storage.api.exception.NotFoundException;
import com.file.storage.api.repository.ApiKeyRepository;
import com.file.storage.api.repository.FileRepository;
import com.file.storage.api.service.FileService;
import com.file.storage.api.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final StorageProvider storageProvider;

    @Override
    public FileUploadResponseDto upload(MultipartFile file, Long apiKey) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String objectKey = UUID.randomUUID().toString();

        try (InputStream in = file.getInputStream()) {
            storageProvider.upload(
                    objectKey,
                    in,
                    file.getSize(),
                    file.getContentType()
            );
        } catch (Exception e) {
            throw new RuntimeException("Storage upload failed", e);
        }

        ApiKeyEntity apiKeyReference = apiKeyRepository.getReferenceById(apiKey);

        FileEntity entity = FileEntity.builder()
                .originalName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .sizeBytes(file.getSize())
                .storageProvider(ProviderEnum.valueOf(storageProvider.name().toUpperCase()))
                .bucket(storageProvider.bucket())
                .objectKey(objectKey)
                .createdBy(apiKeyReference)
                .createdAt(Instant.now())
                .build();

        FileEntity saved = fileRepository.save(entity);

        FileUploadResponseDto response = new FileUploadResponseDto();
        response.setId(saved.getId());
        response.setOriginalName(saved.getOriginalName());
        response.setContentType(saved.getContentType());
        response.setSizeBytes(saved.getSizeBytes());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    @Override
    public FileDownloadResponseDto download(String id, Long apiKeyId) {

        FileEntity file =
                fileRepository.findByIdAndCreatedBy_IdAndDeletedAtIsNull(id, apiKeyId)
                        .orElseThrow(() -> new NotFoundException("File not found"));

        try {
            InputStream stream = storageProvider.download(file.getObjectKey()
            );
            String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();

            FileDownloadResponseDto response = new FileDownloadResponseDto();
            response.setOriginalName(file.getOriginalName());
            response.setContentType(contentType);
            response.setSizeBytes(file.getSizeBytes());
            response.setStream(stream);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Storage download failed", e);
        }
    }


    @Override
    public Page<FileResponseDto> getFileList(Pageable pageable) {

        return fileRepository.findAllByDeletedAtIsNull(pageable).map(fileEntity -> {
            FileResponseDto response = new FileResponseDto();
            response.setId(fileEntity.getId());
            response.setOriginalName(fileEntity.getOriginalName());
            response.setContentType(fileEntity.getContentType());
            response.setSizeBytes(fileEntity.getSizeBytes());
            response.setCreatedAt(fileEntity.getCreatedAt());

            return response;
        });
    }


    @Override
    public void delete(String id, Long apiKeyId) {

        FileEntity file = fileRepository.findByIdAndCreatedBy_IdAndDeletedAtIsNull(id, apiKeyId)
                .orElseThrow(() -> new NotFoundException("File not found"));
        try {
            storageProvider.delete(
                    file.getObjectKey()
            );
        } catch (Exception e) {
            throw new RuntimeException("Storage delete failed", e);
        }

        file.setDeletedAt(Instant.now());
        fileRepository.save(file);
    }
}
