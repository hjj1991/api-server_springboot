package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.application.port.out.auth.model.AccessTokenIssueCommand
import com.hjj.apiserver.application.port.out.auth.model.IssuedAccessToken

interface IssueAccessTokenPort {
    fun issue(command: AccessTokenIssueCommand): IssuedAccessToken
}
