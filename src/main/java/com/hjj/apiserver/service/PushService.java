package com.hjj.apiserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PushService {

    @Value("${line.noti.token}")
    private String lineNotiAccessToken;
    private final WebClient webClient;

    public void pushLineNoti(String message) throws Exception {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("message", message);

        webClient.post()
                .uri(uriBuilder -> uriBuilder.scheme("https")
                        .host("notify-api.line.me")
                        .path("api/notify")
                        .build())
                .body(BodyInserters.fromFormData(formData))
                .headers(httpHeaders -> {
                    httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
                    httpHeaders.add("Authorization","Bearer " + lineNotiAccessToken);
                })
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new Exception()))
                .bodyToMono(Map.class)
                .flux().toStream().findFirst().orElseThrow(Exception::new);
    }

}
