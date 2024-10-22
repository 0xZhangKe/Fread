package com.zhangke.fread.activitypub.app.internal.push

import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.SecureRandom
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtil {

    private const val EC_CURVE_NAME = "prime256v1"

    private val reBase64UrlSafe = """[_-]""".toRegex()

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

    fun decryptData(
        privateKey: String,
        serverPublicKeyEncoded: String,
        data: ByteArray,
    ): String {
        val base64Flag = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        val keySpec = PKCS8EncodedKeySpec(Base64.decode(privateKey, base64Flag))
        val ecPrivateKey = KeyFactory.getInstance("EC").generatePrivate(keySpec) as ECPrivateKey
        val keyFactory = KeyFactory.getInstance("EC")
        val publicKeyBytes = serverPublicKeyEncoded.decodeBase64()
        val serverPublicKey =
            keyFactory.generatePublic(X509EncodedKeySpec(publicKeyBytes)) as ECPublicKey

        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(ecPrivateKey)
        keyAgreement.doPhase(serverPublicKey, true)
        val sharedSecret = keyAgreement.generateSecret()

        val iv = data.copyOfRange(0, 12)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKeySpec = SecretKeySpec(sharedSecret, "AES")
        val parameterSpec = GCMParameterSpec(128, iv) // 128 bit auth tag length

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec)
        val decryptedBytes = cipher.doFinal(data, 12, data.size - 12)
        return String(decryptedBytes)
    }

    fun String.decodeBase64(): ByteArray =
        Base64.decode(
            this,
            if (reBase64UrlSafe.containsMatchIn(this)) Base64.URL_SAFE else Base64.DEFAULT,
        )
}

data class CryptoKeys(
    val privateKey: String,
    val publicKey: String,
    val authKey: String,
)
