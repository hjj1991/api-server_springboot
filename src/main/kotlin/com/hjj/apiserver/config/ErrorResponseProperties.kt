package com.hjj.apiserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.error-response")
class ErrorResponseProperties {
    var problemTypeBaseUri: String = ""
}
