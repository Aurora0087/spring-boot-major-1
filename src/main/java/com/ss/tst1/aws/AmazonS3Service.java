package com.ss.tst1.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
public class AmazonS3Service {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${amazon.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file,String folderName) throws IOException {

        String newFileName = UUID.randomUUID() + file.getOriginalFilename();
        String folderPath = folderName.endsWith("/") ? folderName : folderName + "/";
        String objectKey = folderPath + newFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        s3Client.putObject(bucketName,objectKey,file.getInputStream(),metadata);

        return objectKey;
    }

    public URL generatePreSignedUrl(String key, Date expiration){
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }
}
