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

    public String uploadFiles(MultipartFile file, String nameFile) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        Blob blob = bucket.create("2/423" + nameFile, inputStream, file.getContentType());
        System.out.println("https://firebasestorage.googleapis.com/v0/b/" + firebaseBucket + "/o/" + URLEncoder.encode("2/423" + nameFile, "UTF-8") + "?alt=media");
        return blob.getMediaLink();
    }

    public byte[] downImg() throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);

        Blob blob = bucket.get("2/test");
        ReadChannel reader = blob.reader();
        InputStream inputStream = Channels.newInputStream(reader);


        return IOUtils.toByteArray(inputStream);
    }
}
