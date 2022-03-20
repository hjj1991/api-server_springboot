package com.hjj.apiserver.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FireBaseService {

    @Value("${app.firebase-bucket}")
    private String firebaseBucket;

    public void putProfileImg(byte[] file, String fileName) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        bucket.create(fileName, file, "image/png");
    }

    public void putProfileImg(InputStream file, String fileName) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        bucket.create(fileName, file, "image/jpeg");
    }

    public byte[] getProfileImg(String filePath) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        Blob blob = bucket.get(filePath);
        return blob.getContent();
    }
}
