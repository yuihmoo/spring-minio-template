package com.sample.controller;

import com.sample.dto.request.FileRequest;
import com.sample.service.MinioFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("/file")
@RequiredArgsConstructor
public class MinioFileRestController {
    private final MinioFileService minioFileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadToMinio(@RequestPart("file") MultipartFile file,
                                           @RequestParam("bucket_name") String bucketName) {
        FileRequest fileRequest = FileRequest.builder()
                .file(file)
                .bucketName(bucketName)
                .fileName(file.getOriginalFilename())
                .build();
        minioFileService.uploadObject(fileRequest);
        return ResponseEntity.ok().body(HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteToMinio(@RequestBody FileRequest fileRequest) {
        minioFileService.deleteObject(fileRequest);
        return ResponseEntity.ok().body(HttpStatus.OK);
    }
}
