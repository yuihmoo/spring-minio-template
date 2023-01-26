package com.sample.controller;

import com.sample.controller.handler.MinioNotifyHandler;
import com.sample.service.MinioBucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/bucket")
@RequiredArgsConstructor
public class MinioBucketRestController {
    private final MinioBucketService minioBucketService;
    private final MinioNotifyHandler minioNotifyHandler;

    /**
     * Minio Bucket 생성
     * @param domainName : minio bucket name
     */
    @PostMapping("/create")
    public ResponseEntity<Object> registerDomain(@RequestParam("domain_name") String domainName) {
        minioBucketService.createBucket(domainName);
        return ResponseEntity.ok().body("버켓 생성에 성공하였습니다.");
    }

    /**
     * Minio Notification : 파일 상태 관리 알림 EndPoint
     * @param events : minio event
     */
    @PostMapping("/notify")
    public void fileNotify(@RequestBody LinkedHashMap<String, Object> events) {
        minioNotifyHandler.handleByEventName(events);
    }
}
