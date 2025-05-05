package com.hjj.apiserver.service.impl

//@Service
//class CustomOauth2UserService(
//    private val objectMapper: ObjectMapper,
//    private val tokenProvider: JwtProvider,
//) : DefaultOAuth2UserService() {
//    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
//        if (isModify(userRequest)) {
//            return DefaultOAuth2User(
//                listOf(SimpleGrantedAuthority("ROLE_USER")),
//                mapOf("modify" to true, "provider" to userRequest.clientRegistration.clientName),
//                "modify",
//            )
//        }
//
//        val oAuth2User = super.loadUser(userRequest)
//        val userNameAttributeName =
//            userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
//        val registrationId = userRequest.clientRegistration.registrationId
//        val oAuth2UserAttribute = OAuth2UserAttribute(registrationId, oAuth2User.attributes, userNameAttributeName)
//        val mutableMap = objectMapper.convertValue(oAuth2UserAttribute, Map::class.java).toMutableMap()
//
//        if (isMapping(userRequest)) {
//            mutableMap["mappingUserNo"] =
//                tokenProvider.getUserNoByToken(userRequest.additionalParameters["accessToken"] as String)
//        }
//
//        return oAuth2UserAttribute
//    }
//
//    private fun isModify(userRequest: OAuth2UserRequest): Boolean {
//        return isMapping(userRequest) && userRequest.additionalParameters["modify"] == "true"
//    }
//
//    private fun isMapping(userRequest: OAuth2UserRequest): Boolean {
//        return userRequest.additionalParameters.containsKey("accessToken") &&
//            tokenProvider.isValidToken(userRequest.additionalParameters["accessToken"] as String)
//    }
//}
