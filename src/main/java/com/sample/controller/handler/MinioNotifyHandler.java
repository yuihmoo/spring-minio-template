package com.sample.controller.handler;

import io.minio.messages.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
public class MinioNotifyHandler {

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
            //do something
            System.out.println("notify put " + bucketName +" = "+ objectName);
        }
        else if (eventName.equals(EventType.OBJECT_REMOVED_DELETE.toString())) {
            //do something
            System.out.println("notify delete " + bucketName +" = " + objectName);
        }
        //add eventType
    }
}
