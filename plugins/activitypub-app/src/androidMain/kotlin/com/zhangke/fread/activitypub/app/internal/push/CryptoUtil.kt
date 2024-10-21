package com.zhangke.fread.activitypub.app.internal.push

import android.util.Base64
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec

class CryptoUtil {

    companion object {
        private const val EC_CURVE_NAME = "prime256v1"
    }

    fun generate(): CryptoKeys {
        val base64Flag = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        val generator = KeyPairGenerator.getInstance("EC")
        val spec = ECGenParameterSpec(EC_CURVE_NAME)
        generator.initialize(spec)
        val keyPair = generator.generateKeyPair()
        val authKey = ByteArray(16)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(authKey)
        val encodedPrivateKey = Base64.encodeToString(keyPair.private.encoded, base64Flag)
        val encodedPublicKey = Base64.encodeToString(keyPair.public.encoded, base64Flag)
        val pushAuthKey = Base64.encodeToString(authKey, base64Flag)
        return CryptoKeys(
            privateKey = encodedPrivateKey,
            publicKey = encodedPublicKey,
            authKey = pushAuthKey,
        )
    }
}

data class CryptoKeys(
    val privateKey: String,
    val publicKey: String,
    val authKey: String,
)
