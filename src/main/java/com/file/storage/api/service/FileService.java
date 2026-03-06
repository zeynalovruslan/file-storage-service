package com.file.storage.api.service;

import com.file.storage.api.dto.response.FileDownloadResponseDto;
import com.file.storage.api.dto.response.FileResponseDto;
import com.file.storage.api.dto.response.FileUploadResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;


public interface FileService {

    FileUploadResponseDto upload(MultipartFile file, Long clientKeyId);

    FileDownloadResponseDto download(String id, Long clientKeyId);

    Page<FileResponseDto> getFileList(Pageable pageable);

    void delete(String id, Long clientKeyId);
}
