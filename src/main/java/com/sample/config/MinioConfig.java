package com.sample.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MinioConfig {
    @Value("${minio.access.key}")
    private String accessKey;
    @Value("${minio.access.secret}")
    private String secretKey;
    @Value("${minio.url}")
    private String minioUrl;
    public static final String REGION_OF_SEOUL = "ap-northeast-2";

    /**
     * Minio Client
     * @return
     */
    @Bean
    @Primary
    public MinioClient minioClient() {
        return new MinioClient.Builder()
                .credentials(accessKey, secretKey)
                .endpoint(minioUrl)
                .region(REGION_OF_SEOUL)
                .build();
    }

//    /**
//     * Minio 비동기 Client
//     * @return
//     */
//    public MinioAsyncClient minioAsyncClient() {
//        return new MinioAsyncClient.Builder()
//                .credentials(accessKey, secretKey)
//                .endpoint(minioUrl)
//                .region(REGION_OF_SEOUL)
//                .build();
//    }
}
