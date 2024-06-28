package com.example.demo.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleCloudStorageConfig {
    @Value("${gcp.credentials.location}")
    private Resource gcpCredentialsLocation;

    @Bean
    public Storage googleCloudStorage() throws IOException {
        InputStream credentialsStream = gcpCredentialsLocation.getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }
}
