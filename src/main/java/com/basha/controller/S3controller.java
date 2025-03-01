package com.basha.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.basha.service.S3Service;

@RestController
@CrossOrigin(origins = "*") // Allow frontend to call this API
public class S3controller {

    @Autowired
    private S3Service service;

    // ✅ Upload PDF to S3 and return file URL
    @PostMapping("/upload")
    
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = service.upload(file);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Resume uploaded successfully!");
            response.put("fileUrl", fileUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to upload resume.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ✅ Download Resume as PDF
    @GetMapping("/getResume/{fileName}")
    public ResponseEntity<ByteArrayResource> getResume(@PathVariable String fileName) {
        ByteArrayResource resource = service.getResume(fileName);
        if (resource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}