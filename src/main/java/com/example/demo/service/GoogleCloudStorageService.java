package com.example.demo.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoogleCloudStorageService {
    private final Storage storage;

    @Autowired
    public GoogleCloudStorageService(Storage storage) {
        this.storage = storage;
    }

    public void uploadFile(String bucketName, String objectName, byte[] data) {
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.create(objectName, data);
    }

    public byte[] downloadFile(String bucketName, String objectName) {
        Blob blob = storage.get(bucketName).get(objectName);
        return blob.getContent();
    }
}
