package com.example.demo.util;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.auth.oauth2.GoogleCredentials;


import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class VertexAIConfig {

    @Value("${gcp.credentials.location}")
    private Resource credentialsPath;

    @Bean
    public PredictionServiceClient predictionServiceClient() throws IOException, IOException {
        InputStream credentialsStream = credentialsPath.getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        PredictionServiceSettings serviceSettings = PredictionServiceSettings.newBuilder()
                .setEndpoint("europe-west4-aiplatform.googleapis.com:443")
                //.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        return PredictionServiceClient.create(serviceSettings);
    }
}
