package com.example.demo.controller;

import com.example.demo.service.GoogleCloudPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
public class PredictionController {

    @Autowired
    private GoogleCloudPredictionService predictionService;

    @PostMapping("/v1/detectImage/{username}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<byte[]> detectImage(@RequestParam("file") MultipartFile file, @PathVariable("username") String username) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        byte[] byteArray= predictionService.getPrediciton(file.getBytes());
        return  new ResponseEntity<>(byteArray, headers, HttpStatus.OK);
    }
}
