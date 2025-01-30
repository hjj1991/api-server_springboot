package com.hjj.apiserver.common.filter

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import mu.two.KotlinLogging
import org.apache.commons.io.IOUtils
import org.springframework.web.util.ContentCachingRequestWrapper
import java.io.ByteArrayInputStream

private val log = KotlinLogging.logger {}

class CustomContentCachingRequestWrapper(request: HttpServletRequest) : ContentCachingRequestWrapper(request) {
    private val inputStream = IOUtils.toByteArray(super.getInputStream())
    private lateinit var multipartParameterNames: Set<String>

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(inputStream)

        return object : ServletInputStream() {
            override fun read(): Int = byteArrayInputStream.read()

            override fun isFinished(): Boolean {
                return kotlin.runCatching {
                    byteArrayInputStream.available() == 0
                }.onFailure { exception ->
                    log.error(exception) { "[CustomContentCachingRequestWrapper] error happend: ${exception.message}" }
                }.getOrDefault(false)
            }

            override fun isReady(): Boolean = false

            override fun setReadListener(readListener: ReadListener) {}
        }
    }
}
