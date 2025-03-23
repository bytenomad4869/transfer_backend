package com.acme;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

public class MinIO {
    private String url;
    private int port;
    private boolean secure;
    private String region;
    private String accessKey;
    private String secretKey;
    private MinioClient minioClient;

    public MinIO() {
        Config config = ConfigProvider.getConfig();
        this.url = config.getValue("quarkus.minio.url", String.class);
        this.port = config.getValue("quarkus.minio.port", Integer.class);
        this.secure = config.getValue("quarkus.minio.secure", Boolean.class);
        this.region = "eu-central-1";
        this.accessKey = config.getValue("quarkus.minio.access-key", String.class);
        this.secretKey = config.getValue("quarkus.minio.secret-key", String.class);

        this.minioClient = MinioClient.builder()
                .endpoint(url, port, secure)
                .region(region)
                .credentials(accessKey, secretKey)
                .build();
    }

    public void makeBucket(String bucketName) {
        try {
            this.minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importFile(String bucketName, String objectName, Path filePath) {
        try (InputStream in = new FileInputStream(filePath.toFile())) {
            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(in, filePath.toFile().length(), -1)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
