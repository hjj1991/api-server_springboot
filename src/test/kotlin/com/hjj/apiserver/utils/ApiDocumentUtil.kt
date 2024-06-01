package com.hjj.apiserver.utils

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation

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

        fun getBaseErrorResponse(): List<FieldDescriptor> {
            return listOf(
                PayloadDocumentation.fieldWithPath("errCode").description("에러코드"),
                PayloadDocumentation.fieldWithPath("message").description("에러메시지"),
                PayloadDocumentation.fieldWithPath("data").description("에러 상세필드"),
            )
        }
    }
}
