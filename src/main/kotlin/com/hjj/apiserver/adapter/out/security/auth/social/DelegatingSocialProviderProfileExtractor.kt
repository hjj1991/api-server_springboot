package com.hjj.apiserver.adapter.out.security.auth.social

import com.hjj.apiserver.common.ErrConst
import com.hjj.apiserver.common.exception.BaseException
import org.springframework.stereotype.Component

@Component
class DelegatingSocialProviderProfileExtractor(
    extractors: List<SocialProviderProfileExtractor>,
) {
    private val extractorsByRegistrationId = extractors

    fun extract(
        registrationId: String,
        attributes: Map<String, Any>,
    ): SocialProviderProfile {
        val extractor =
            extractorsByRegistrationId.firstOrNull { it.supports(registrationId) }
                ?: throw BaseException(ErrConst.ERR_CODE0016, "지원하지 않는 소셜 공급자입니다.")

        return extractor.extract(attributes)
    }
}
