package com.hjj.apiserver.adapter.input.web.admin

import com.hjj.apiserver.adapter.input.web.ApiVersionConstants
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminOverviewController {
    @GetMapping("/overview", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.OK)
    fun overview(
        @AuthenticationPrincipal jwt: Jwt,
    ): AdminOverviewResponse =
        AdminOverviewResponse(
            userId = jwt.subject.toLong(),
            roles = jwt.getClaimAsStringList("roles") ?: emptyList(),
            sections =
                listOf(
                    "content",
                    "account",
                    "release",
                ),
        )
}

data class AdminOverviewResponse(
    val userId: Long,
    val roles: List<String>,
    val sections: List<String>,
)
