package com.sample.service.runnable;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Paths;

@RequiredArgsConstructor
class PutObjectRunnable implements Runnable {
    private MinioClient client;
    private String bucketName;
    private String filename;

    public PutObjectRunnable(MinioClient client, String bucketName, String filename) {
        this.client = client;
        this.bucketName = bucketName;
        this.filename = filename;
    }

    /**
     * 아래 예제는 스레드를 이용하여 업로드 & 삭제
     */
    public void run() {
        StringBuffer traceBuffer = new StringBuffer();

        try {
            traceBuffer.append("[").append(filename).append("]: threaded put object\n");
            client.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .filename(filename)
                            .build());
            traceBuffer.append("[").append(filename).append("]: delete file\n");
            Files.delete(Paths.get(filename));
            traceBuffer.append("[").append(filename).append("]: delete object\n");
            client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(filename).build());
        } catch (Exception e) {
            System.err.print(traceBuffer);
            e.printStackTrace();
        }
    }
}
