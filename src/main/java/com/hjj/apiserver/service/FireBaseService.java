package com.hjj.apiserver.service;

import com.google.api.services.storage.Storage;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FireBaseService {

    @Value("${app.firebase-bucket}")
    private String firebaseBucket;

    public void uploadFiles(MultipartFile file, String fileName) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
//        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        bucket.create(fileName, file.getInputStream(), file.getContentType());
    }

    public byte[] getProfileImg(String filePath) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);

        Blob blob = bucket.get(filePath);


        return blob.getContent();
    }
}
