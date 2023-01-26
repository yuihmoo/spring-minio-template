package com.sample.controller.handler;

import com.sample.service.MinioBucketService;
import io.minio.messages.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
public class MinioNotifyHandler {
    private final MinioBucketService minioBucketService;

    /**
     * File 상태 변경에 따라 Method Handling
     * @param events : Minio events
     */
    public void handleByEventName(LinkedHashMap<String, Object> events) {
        String eventName = String.valueOf(events.get("EventName"));
        String value = String.valueOf(events.get("Key"));
        String[] mapValues = value.split("/");
        String bucketName = mapValues[0];
        String objectName = mapValues[1];

        if(eventName.equals(EventType.OBJECT_CREATED_PUT.toString())) {
            minioBucketService.recordCreateLog(bucketName, objectName);
        }
        else if (eventName.equals(EventType.OBJECT_REMOVED_DELETE.toString())) {
            minioBucketService.recordDeleteLog(bucketName, objectName);
        }
    }
}
