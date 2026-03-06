package com.file.storage.api.repository;

import com.file.storage.api.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, String> {

    Optional<FileEntity> findByIdAndOwnerKey_IdAndRemovedAtIsNull(String id, Long apiKeyId);

    Page<FileEntity> findAllByRemovedAtIsNull(Pageable pageable);

}
