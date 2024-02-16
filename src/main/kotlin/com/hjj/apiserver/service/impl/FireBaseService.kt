package com.hjj.apiserver.service.impl

import com.google.cloud.storage.Bucket
import com.google.firebase.cloud.StorageClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class FireBaseService(
    @Value("\${app.firebase-bucket}")
    private val firebaseBucket: String,
) {
    fun putProfileImg(
        file: ByteArray,
        fileName: String,
    ) {
        val bucket: Bucket = StorageClient.getInstance().bucket(firebaseBucket)
        bucket.create(fileName, file, "image/png")
    }

    fun putProfileImg(
        file: InputStream,
        fileName: String,
    ) {
        val bucket: Bucket = StorageClient.getInstance().bucket(firebaseBucket)
        bucket.create(fileName, file, "image/jpeg")
    }

    fun getProfileImg(filePath: String): ByteArray {
        val bucket: Bucket = StorageClient.getInstance().bucket(firebaseBucket)
        return bucket.get(filePath).getContent()
    }
}
