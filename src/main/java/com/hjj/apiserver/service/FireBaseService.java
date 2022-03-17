package com.hjj.apiserver.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FireBaseService {

    @Value("${app.firebase-bucket}")
    private String firebaseBucket;

    public String uploadFiles(MultipartFile file, String nameFile) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        Blob blob = bucket.create("2/" + nameFile, inputStream, file.getContentType());
        System.out.println("blob = " + blob.getMediaLink());
        return blob.getMediaLink();
    }
}
