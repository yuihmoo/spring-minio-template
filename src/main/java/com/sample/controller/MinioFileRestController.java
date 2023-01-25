package com.sample.controller;

import com.sample.dto.request.FileRequest;
import com.sample.dto.response.BaseResponse;
import com.sample.service.MinioFileService;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class MinioFileRestController {
    private final MinioFileService minioFileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFileToMinio(@ModelAttribute FileRequest fileRequest) {
        minioFileService.uploadObject(fileRequest);
        return ResponseEntity.ok().body("업로드에 성공하였습니다.");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteFileToMinio(@ModelAttribute FileRequest fileRequest) {
        minioFileService.deleteObject(fileRequest);
        return ResponseEntity.ok().body("삭제 하였습니다.");
    }

    @PostMapping(value = "/download")
    public ResponseEntity<?> getFileFromMinio(@ModelAttribute FileRequest fileRequest) {
        String preSignedUrl = minioFileService.getPreSignedUrl(fileRequest, Method.GET);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.builder().code("200").message("").data(preSignedUrl).build());
    }
}
