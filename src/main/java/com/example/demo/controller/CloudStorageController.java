package com.example.demo.controller;

import com.example.demo.auth.AuthenticationResponse;
import com.example.demo.service.GoogleCloudStorageService;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
public class CloudStorageController {

    @Autowired
    GoogleCloudStorageService googleCloudStorageService;

    @PostMapping("/v1/postImage/{username}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<String> postImage(@RequestParam("file") MultipartFile file,
                                            @PathVariable("username") String username) throws IOException {
        byte[] arrayFileImage= file.getBytes();
        googleCloudStorageService.uploadFile("users-bucket-dental-forecasting","user-images/"+username+"-"+ UUID.randomUUID()+".jpeg",arrayFileImage);
        return ResponseEntity.ok("OK");
    }

}
