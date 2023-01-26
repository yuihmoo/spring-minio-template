package com.sample.service;

import com.sample.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinioBucketService {
    private final Logger logger = LoggerFactory.getLogger(MinioBucketService.class);
    private final MinioClient minioClient;

    /**
     * 해당 Minio Bucket 존재하는지 확인
     * @param bucketName : Minio Bucket Name
     * @return : boolean
     */
    public boolean isExistBucket(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
               NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
               InternalException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Minio Bucket 생성
     * @param bucketName : Minio Bucket Name
     */
    public void createBucket(String bucketName) {
        boolean isBucket = isExistBucket(bucketName);
        if (!isBucket) {
            try {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .region(MinioConfig.REGION_OF_SEOUL)
                        .build());
            }
            catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                logger.error("MinioFileService createBucket() -> BucketName : " + " Created " + bucketName);
                throw new RuntimeException(e);
            }
        }
        else {
            logger.info("MinioFileService createBucket() -> BucketName : " + "already exist " + bucketName);
        }
    }

    /**
     * Minio Bucket 삭제
     * @param bucketName : Minio Bucket Name
     */
    public void deleteBucket(String bucketName) {
        if(isExistBucket(bucketName)) {
            try {
                minioClient.removeBucket(RemoveBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
            catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                logger.info("MinioFileService deleteBucket() -> " + "Success " + bucketName);
            }
        }
    }

    /**
     * Minio Bucket AES256 Encrypt 설정
     * @param bucketName : Minio Bucket Name
     */
    public void setBucketEncryption(String bucketName) {
        try {
            minioClient.setBucketEncryption(
                    SetBucketEncryptionArgs.builder()
                            .bucket(bucketName)
                            .config(SseConfiguration.newConfigWithSseS3Rule())
                            .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 버킷 WebHook 알림 기능 설정
     * @param bucketName : Minio Bucket Name
     */
    public void setWebHookNotificationOfBucket(String bucketName) {
        try {
            String allFileSuffix = ".*";
            String defaultQueue = "arn:minio:sqs::doubled:webhook";

            NotificationConfiguration config =
                    minioClient.getBucketNotification(GetBucketNotificationArgs.builder().bucket(bucketName).build());

            // Add a new SQS configuration.
            List<QueueConfiguration> queueConfigurationList = new LinkedList<>();
            QueueConfiguration queueConfiguration = new QueueConfiguration();
            queueConfiguration.setQueue(defaultQueue);

            List<EventType> eventList = new LinkedList<>();
            eventList.add(EventType.OBJECT_CREATED_PUT);
            eventList.add(EventType.OBJECT_REMOVED_DELETE);
            queueConfiguration.setEvents(eventList);
            queueConfiguration.setSuffixRule(allFileSuffix);

            queueConfigurationList.add(queueConfiguration);
            config.setQueueConfigurationList(queueConfigurationList);

            // Set updated notification configuration.
            minioClient.setBucketNotification(SetBucketNotificationArgs.builder()
                    .bucket(bucketName)
                    .config(config)
                    .build());
            logger.info("MinioFileService setWebHookNotificationOfBucket() -> BucketName : " + bucketName);
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error("MinioFileService setWebHookNotificationOfBucket() -> BucketName : " + bucketName);
        }
    }

    /**
     * Bucket List 조회
     * @return : 버켓 객체 리스트
     */
    public List<Bucket> getBucketList() {
        try {
            return minioClient.listBuckets(ListBucketsArgs.builder().build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    public void recordCreateLog(String bucketName, String objectName) {
        StringBuilder stringBuilder = new StringBuilder();
        logger.info(stringBuilder.append(bucketName)
                .append(" ").append("내에").append(objectName).append("이(가) 성공적으로 생성 되었습니다.").toString());
    }

    public void recordDeleteLog(String bucketName, String objectName) {
        StringBuilder stringBuilder = new StringBuilder();
        logger.info(stringBuilder.append(bucketName)
                .append(" ").append("내에").append(objectName).append("이(가) 성공적으로 삭제 되었습니다.").toString());
    }
}
