package com.hjj.apiserver.utils

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.*

class ApiDocumentUtil {
    companion object {
        fun getDocumentRequest(): OperationRequestPreprocessor { // (10)
            return preprocessRequest(
                modifyUris()
                    .scheme("http")
                    .host("user.api.com")
                    .removePort(),
                prettyPrint(),
            )
        }

        fun getDocumentResponse(): OperationResponsePreprocessor { // (11)
            return preprocessResponse(prettyPrint())
        }
    }
}
