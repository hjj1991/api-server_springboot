package com.hjj.apiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ApiServerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ApiServerApplication.class);
        app.setRegisterShutdownHook(false);
        app.run(args);
    }

}
