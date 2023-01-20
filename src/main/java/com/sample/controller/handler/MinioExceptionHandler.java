package com.sample.controller.handler;

import com.sample.dto.response.BaseResponse;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class MinioExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(MinioExceptionHandler.class);

    @ExceptionHandler(MinioException.class)
    public ResponseEntity<?> handleUnSignedUserException(MinioException exception) {
        logger.error(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.builder()
                .code("Error")
                .message(exception.getMessage())
                .build());
    }
}
