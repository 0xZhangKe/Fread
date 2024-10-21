package com.zhangke.fread.activitypub.app.internal.push

import android.util.Base64
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.SecureRandom
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.util.Arrays

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
        val encodedPublicKey =
            Base64.encodeToString(serializeRawPublicKey(keyPair.public), base64Flag)
        val pushAuthKey = Base64.encodeToString(authKey, base64Flag)
        return CryptoKeys(
            privateKey = encodedPrivateKey,
            publicKey = encodedPublicKey,
            authKey = pushAuthKey,
        )
    }

    private fun serializeRawPublicKey(key: PublicKey): ByteArray {
        val point = (key as ECPublicKey).w
        var x = point.affineX.toByteArray()
        var y = point.affineY.toByteArray()
        if (x.size > 32) x = Arrays.copyOfRange(x, x.size - 32, x.size)
        if (y.size > 32) y = Arrays.copyOfRange(y, y.size - 32, y.size)
        val result = ByteArray(65)
        result[0] = 4
        System.arraycopy(x, 0, result, 1 + (32 - x.size), x.size)
        System.arraycopy(y, 0, result, result.size - y.size, y.size)
        return result
    }
}

data class CryptoKeys(
    val privateKey: String,
    val publicKey: String,
    val authKey: String,
)
