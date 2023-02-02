package com.sample.controller;

import com.sample.dto.request.FileRequest;
import com.sample.dto.response.BaseResponse;
import com.sample.dto.response.FileResponse;
import com.sample.service.MinioFileService;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class MinioFileRestController {
    private final MinioFileService minioFileService;

    @PostMapping()
    public ResponseEntity<?> uploadFileToMinio(@ModelAttribute FileRequest fileRequest) {
        minioFileService.uploadObject(fileRequest);
        return ResponseEntity.ok().body("업로드에 성공하였습니다.");
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteFileToMinio(@ModelAttribute FileRequest fileRequest) {
        minioFileService.deleteObject(fileRequest);
        return ResponseEntity.ok().body("삭제 하였습니다.");
    }

    @GetMapping()
    public ResponseEntity<?> getFileFromMinio(@ModelAttribute FileRequest fileRequest) {
        FileResponse fileResponse = minioFileService.getObject(fileRequest.getBucketName(), fileRequest.getFileName());
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.builder().code("200").message("").data(fileResponse).build());
    }

    @GetMapping("/pre-signed")
    public ResponseEntity<?> getPreSignedToMinio(@ModelAttribute FileRequest fileRequest) {
        String preSignedUrl = minioFileService.getPreSignedUrl(fileRequest, Method.GET);
        BaseResponse baseResponse = new BaseResponse("200", "", preSignedUrl);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.builder().code("200").message("").data(baseResponse).build());
    }
}
