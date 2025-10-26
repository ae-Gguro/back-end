package com.example.gguro.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.gguro.config.S3Config;
import com.example.gguro.domain.Uuid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;
    private final S3Config s3Config;

    public String uploadFile(String keyName, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(s3Config.getBucket(), keyName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }

        return amazonS3.getUrl(s3Config.getBucket(), keyName).toString(); // 업로드된 파일의 URL
    }

    public String generateProfileImageKeyName(Uuid uuid) {
        return s3Config.getProfileImagePath() + "/" + uuid.getUuid();
    }

}
