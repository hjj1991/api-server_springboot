package com.hjj.apiserver.service.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
class PushService(
    @Value("\${line.noti.token}")
    private val lineNotiAccessToken: String,
    private val webClient: WebClient,
) {

    fun pushLineNoti(message: String){
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("message", message)

        webClient.post()
            .uri{
                it.scheme("https")
                    .host("notify-api.line.me")
                    .path("api/notify")
                    .build()
            }
            .body(BodyInserters.fromFormData(formData))
            .headers {
                it.add("Content-Type", "application/x-www-form-urlencoded");
                it.add("Authorization", "Bearer ${lineNotiAccessToken}");
            }
            .retrieve()
            .bodyToMono(Unit::class.java)
    }

}