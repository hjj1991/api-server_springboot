package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.domain.auth.AuthProviderType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.HexFormat
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AuthCryptoService(
    @Value("\${app.auth.crypto-secret:\${spring.jwt.secret}}")
    secret: String,
) {
    private val secureRandom = SecureRandom()
    private val hexFormat = HexFormat.of()
    private val base64UrlEncoder = Base64.getUrlEncoder().withoutPadding()
    private val base64UrlDecoder = Base64.getUrlDecoder()
    private val hmacKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), HMAC_ALGORITHM)
    private val aesKey = SecretKeySpec(MessageDigest.getInstance("SHA-256").digest(secret.toByteArray(StandardCharsets.UTF_8)), AES_ALGORITHM)

    fun normalizeEmail(email: String): String = email.trim().lowercase()

    fun normalizeLoginId(loginId: String): String = loginId.trim().lowercase()

    fun createEmailLookupHash(email: String): String = this.hmacHex(this.normalizeEmail(email))

    fun createVerificationToken(): String {
        val bytes = ByteArray(TOKEN_BYTE_SIZE)
        secureRandom.nextBytes(bytes)
        return base64UrlEncoder.encodeToString(bytes)
    }

    fun createVerificationTokenHash(token: String): String = this.hmacHex(token)

    fun createOpaqueToken(): String = this.createVerificationToken()

    fun createOpaqueTokenHash(token: String): String = this.createVerificationTokenHash(token)

    fun createProviderSubjectHash(
        providerType: AuthProviderType,
        providerSubject: String,
    ): String = this.hmacHex("${providerType.name}:${providerSubject.trim()}")

    fun createLocalProviderSubjectHash(): String = this.hmacHex("${AuthProviderType.LOCAL.name}:${UUID.randomUUID()}")

    fun encryptEmail(email: String): String {
        val iv = ByteArray(GCM_IV_SIZE)
        secureRandom.nextBytes(iv)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, aesKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        }
        val ciphertext = cipher.doFinal(this.normalizeEmail(email).toByteArray(StandardCharsets.UTF_8))
        return "${base64UrlEncoder.encodeToString(iv)}.${base64UrlEncoder.encodeToString(ciphertext)}"
    }

    fun decryptEmail(ciphertext: String): String {
        val (ivEncoded, encryptedEncoded) = ciphertext.split('.', limit = 2)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(
                Cipher.DECRYPT_MODE,
                aesKey,
                GCMParameterSpec(GCM_TAG_LENGTH, base64UrlDecoder.decode(ivEncoded)),
            )
        }
        return String(cipher.doFinal(base64UrlDecoder.decode(encryptedEncoded)), StandardCharsets.UTF_8)
    }

    private fun hmacHex(value: String): String {
        val mac = Mac.getInstance(HMAC_ALGORITHM).apply { init(hmacKey) }
        return hexFormat.formatHex(mac.doFinal(value.toByteArray(StandardCharsets.UTF_8)))
    }

    private companion object {
        const val HMAC_ALGORITHM = "HmacSHA256"
        const val AES_ALGORITHM = "AES"
        const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
        const val GCM_IV_SIZE = 12
        const val GCM_TAG_LENGTH = 128
        const val TOKEN_BYTE_SIZE = 32
    }
}
