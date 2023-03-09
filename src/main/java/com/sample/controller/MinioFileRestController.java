package com.sample.controller;

import com.sample.dto.request.FileRequest;
import com.sample.dto.request.FileSearchRequest;
import com.sample.dto.response.BaseResponse;
import com.sample.dto.response.FileResponse;
import com.sample.service.ImageService;
import com.sample.service.MinioFileService;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class MinioFileRestController {
    private final MinioFileService minioFileService;
    private final ImageService imageService;

    @PostMapping()
    public ResponseEntity<?> uploadFileToMinio(@ModelAttribute FileRequest fileRequest) {
        if(fileRequest.getFileInfo() == null) {
            minioFileService.uploadObject(fileRequest);
        }
        else {
            minioFileService.uploadObjectWithTags(fileRequest, fileRequest.getFileInfo());
        }
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.builder().message("업로드에 성공하였습니다.").build());
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteFileToMinio(@ModelAttribute FileRequest fileRequest) {
        minioFileService.deleteObject(fileRequest);
        return ResponseEntity.ok().body(BaseResponse.builder().message("삭제에 성공하였습니다.").build());
    }

    @GetMapping()
    public ResponseEntity<?> getFileFromMinio(@ModelAttribute FileRequest fileRequest) {
        FileResponse fileResponse = minioFileService.getObject(fileRequest.getBucketName(), fileRequest.getFileName());
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.builder().message("").data(fileResponse).build());
    }

    @GetMapping("/pre-signed")
    public ResponseEntity<?> getPreSignedToMinio(@ModelAttribute FileRequest fileRequest) {
        String preSignedUrl = minioFileService.getPreSignedUrl(fileRequest, Method.GET);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.builder().code("200").message("").data(preSignedUrl).build());
    }

    @GetMapping("/list")
    public ResponseEntity<?> getFileList(@RequestParam("bucket_name") String bucketName) {
        List<FileResponse> fileResponses = minioFileService.getListObjects(bucketName);
        return ResponseEntity.status(HttpStatus.OK).body(fileResponses);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFileList(@RequestBody FileSearchRequest fileSearchRequest) throws Exception {
        ArrayList<FileResponse> fileResponses =
                minioFileService.getObjectNamesByKeyword(fileSearchRequest.getUsername(), fileSearchRequest.getKeyword());
        return ResponseEntity.status(HttpStatus.OK).body(fileResponses);
    }

    @GetMapping("/tags")
    public ResponseEntity<?> getFileOfTags(@ModelAttribute FileRequest fileRequest) {
        Map<String, String> fileResponses =
                minioFileService.getObjectTags(fileRequest);
        return ResponseEntity.status(HttpStatus.OK).body(fileResponses);
    }

    @PatchMapping("/tags")
    public ResponseEntity<?> setFileOfTags(@ModelAttribute FileRequest fileRequest) {
        minioFileService.setObjectTags(fileRequest);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/thumbnail")
    public ResponseEntity<?> createThumbnail(@ModelAttribute FileRequest fileRequest) throws Exception {
        minioFileService.uploadThumbnail(fileRequest, 300, 300);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.builder().message("업로드에 성공하였습니다.").build());
    }
}
