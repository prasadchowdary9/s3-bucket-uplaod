package com.basha.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class S3Service {

    private final AmazonS3 s3Client;
    
    @Value("${aws.bucket}")
    private String bucketName;

    public S3Service(
        @Value("${aws.accessKeyId}") String accessKey,
        @Value("${aws.secretAccessKey}") String secretKey,
        @Value("${aws.region}") String region
    ) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    // ✅ Upload Resume to S3 and return the file URL
    public String upload(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize()); // Set content length

            // Upload to S3
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));

            // Generate file URL
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    // ✅ Retrieve PDF from S3 as ByteArrayResource
    public ByteArrayResource getResume(String fileName) {
        try {
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, outputStream);

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            return null; // File not found or error
        }
    }
}
