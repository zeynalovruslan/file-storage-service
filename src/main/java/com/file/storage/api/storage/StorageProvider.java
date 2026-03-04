package com.file.storage.api.storage;

import java.io.InputStream;

public interface StorageProvider {

    String name();

    void upload(String objectKey, InputStream data, long size, String contentType);

    InputStream download(String objectKey);

    void delete(String objectKey);

    String bucket();
}
