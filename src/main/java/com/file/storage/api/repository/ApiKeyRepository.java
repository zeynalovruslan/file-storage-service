package com.file.storage.api.repository;

import com.file.storage.api.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Long> {

    Optional<ApiKeyEntity> findByKeyPrefixAndActiveTrue(String keyPrefix);

    boolean existsByName(String name);

}
