package com.file.storage.api.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConditionalOnProperty(name = "storage.provider", havingValue = "local")
public class LocalStorageProvider implements StorageProvider {

    @Value("${storage.local.base-path}")
    private String basePath;

    @Override
    public String name() {
        return "local";
    }

    @Override
    public void upload(String objectKey, InputStream data, long size, String contentType) {
        try {
            Path path = Paths.get(basePath, objectKey);

            Files.createDirectories(path.getParent());
            Files.copy(data, path);

        } catch (IOException e) {
            throw new RuntimeException("Local storage upload failed", e);
        }
    }

    @Override
    public InputStream download(String objectKey) {

        try {
            Path path = Paths.get(basePath, objectKey);
            return Files.newInputStream(path);

        } catch (IOException e) {
            throw new RuntimeException("Local storage download failed", e);
        }
    }

    @Override
    public void delete(String objectKey) {

        try {
            Path path = Paths.get(basePath, objectKey);
            Files.deleteIfExists(path);

        } catch (IOException e) {
            throw new RuntimeException("Local storage delete failed", e);
        }
    }

    @Override
    public String bucket() {
        return "local";
    }

}
