package com.sample.service;

import com.sample.dto.request.FileRequest;
import com.sample.dto.response.FileResponse;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import io.minio.messages.Tags;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioFileService {
    private final Logger logger = LoggerFactory.getLogger(MinioFileService.class);
    private final MinioClient minioClient;

    /**
     * Minio Object 존재 확인
     * Reference : Minio 에서 제공해주는 메서드가 없는 관계로 statObject 의 콜백 값을 사용
     * @param bucketName : Minio Bucket Name
     * @param objectName : Minio File Name
     * @return : boolean
     */
    public boolean isObjectExist(String bucketName, String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName).build());
            return true;
        } catch (ErrorResponseException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 서버 -> Minio 파일 직접 업로드
     * @param fileRequest : file instance
     */
    public void uploadObject(FileRequest fileRequest) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(fileRequest.getBucketName())
                    .object(fileRequest.getFileName())
                    .contentType(fileRequest.getFile().getContentType())
                    .stream(fileRequest.getFile().getInputStream(), fileRequest.getFile().getSize(), -1)
                    .build());
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 서버 -> Minio 다중 파일 직접 업로드
     * @param fileRequestList : file instance list
     */
    public void uploadObjects(List<FileRequest> fileRequestList) {
        try {
            for (FileRequest fileRequest : fileRequestList) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(fileRequest.getBucketName())
                        .object(fileRequest.getFileName())
                        .contentType(fileRequest.getFile().getContentType())
                        .stream(fileRequest.getFile().getInputStream(), fileRequest.getFile().getSize(), -1)
                        .build());
                System.out.println(fileRequest.getFile() + "uploaded");
            }
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 서버 -> Minio 파일 직접 업로드(Tags 포함)
     *
     * @param fileRequest : file instance
     * @param fileInfo    : file Info map
     */
    public void uploadObjectWithTags(FileRequest fileRequest, Map<String, String> fileInfo) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(fileRequest.getBucketName())
                    .object(fileRequest.getFileName())
                    .contentType(fileRequest.getFile().getContentType())
                    .tags(fileInfo)
                    .stream(fileRequest.getFile().getInputStream(), fileRequest.getFile().getSize(), -1)
                    .build());
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 서버 -> Minio 파일 직접 삭제
     * @param fileRequest : File instance
     */
    public void deleteObject(FileRequest fileRequest) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(fileRequest.getBucketName())
                    .object(fileRequest.getFileName())
                    .build());
        }
        catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * minio File 서버 인증 URL 가져오기
     * @param fileRequest : File instance
     * @param method : Enum
     * @return : Minio PreSignedUrl
     */
    public String getPreSignedUrl(FileRequest fileRequest, Method method) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(method)
                            .bucket(fileRequest.getBucketName())
                            .object(fileRequest.getFileName())
                            .expiry(1, TimeUnit.MINUTES)
                            .build());
        }
        catch (RuntimeException | ErrorResponseException | InsufficientDataException | InternalException |
               InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException |
               XmlParserException | ServerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 특정 파일 태그 변경
     * @param map : File Info
     * @param fileRequest : File instance
     */
    public void setObjectTags(HashMap<String, String> map, FileRequest fileRequest) {
        try {
            minioClient.setObjectTags(
                    SetObjectTagsArgs.builder()
                            .bucket(fileRequest.getBucketName())
                            .object(fileRequest.getFileName())
                            .tags(map)
                            .build());
            logger.info("MinioMapService setObjectTags() -> objectName : " + fileRequest.getFileName() + "tags : " + map);
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error("MinioMapService setObjectTags() -> objectName : " + fileRequest.getFileName() + "tags : " + map);
            throw new RuntimeException(e);
        }
    }

    /**
     * 특정 파일 태그 조회
     * @param fileRequest : file instance
     * @return : Minio Tags
     */
    public Map<String, String> getObjectTags(FileRequest fileRequest) {
        try {
            Tags tags = minioClient.getObjectTags(
                    GetObjectTagsArgs.builder()
                            .bucket(fileRequest.getBucketName())
                            .object(fileRequest.getFileName())
                            .build());
            return tags.get();
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Minio Object 조회
     * @param bucketName : minio bucket name
     * @param objectName : minio object name
     * @return : Minio object
     */
    public FileResponse getObject(String bucketName, String objectName) {
        try {
            GetObjectResponse item = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return FileResponse.builder().fileName(item.object()).build();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Minio Object 조회 (With 태그값)
     * @param bucketName : minio bucket Name
     * @param objectName : minio object Name
     * @param keyword : minio etag value
     * @return : minio object
     */
    public FileResponse getObjectWithEtag(String bucketName, String objectName, String keyword) {
        try {
            GetObjectResponse item = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .matchETag(keyword)
                    .build());
            return FileResponse.builder().fileName(item.object()).build();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Minio Bucket 안에 Object List 조회 (Default 100)
     * @param bucketName : Minio Bucket Name
     * @return : 파일 객체 리스트
     */
    public List<FileResponse> getListObjects(String bucketName) {
        try {
            List<FileResponse> objects = new ArrayList<>();
            Iterable<Result<Item>> result = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(true)
                    .maxKeys(100)
                    .build());
            for (Result<Item> item : result) {
                objects.add(FileResponse.builder()
                        .fileName(item.get().objectName())
                        .build());
                return objects;
            }
            return objects;
        }
        catch (ServerException | InsufficientDataException | NoSuchAlgorithmException | InvalidKeyException |
                 ErrorResponseException | IOException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 버켓 내에 키워드에 해당하는 각 파일들 조회
     * @param username : 유저 아이디
     * @param keyword : 검색할 이름
     * @return : 파일 객체 리스트
     * @throws Exception
     */
    public ArrayList<FileRequest> getObjectNamesByKeyword(String username, String keyword) throws Exception {
        ArrayList<FileRequest> fileDtoList = new ArrayList<>();
        Iterable<Result<Item>> result = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(username)
                .recursive(true)
                .prefix(keyword)
                .maxKeys(10)
                .build());

        for (Result<Item> item : result) {
            FileRequest fileDto = FileRequest.builder()
                    .bucketName(username)
                    .fileName(item.get().objectName())
                    .build();

            fileDtoList.add(fileDto);
        }
        return fileDtoList;
    }
}
