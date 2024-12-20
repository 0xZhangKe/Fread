package com.zhangke.fread.activitypub.app.internal.push

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.SecureRandom
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtil {

    private const val EC_CURVE_NAME = "prime256v1"

    private val P256_HEAD = byteArrayOf(
        0x30.toByte(),
        0x59.toByte(),
        0x30.toByte(),
        0x13.toByte(),
        0x06.toByte(),
        0x07.toByte(),
        0x2a.toByte(),
        0x86.toByte(),
        0x48.toByte(),
        0xce.toByte(),
        0x3d.toByte(),
        0x02.toByte(),
        0x01.toByte(),
        0x06.toByte(),
        0x08.toByte(),
        0x2a.toByte(),
        0x86.toByte(),
        0x48.toByte(),
        0xce.toByte(),
        0x3d.toByte(),
        0x03.toByte(),
        0x01.toByte(),
        0x07.toByte(),
        0x03.toByte(),
        0x42.toByte(),
        0x00.toByte()
    )

    private val reBase64UrlSafe = """[_-]""".toRegex()

    fun generate(): CryptoKeys {
        val base64Flag = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        val generator = KeyPairGenerator.getInstance("EC")
        generator.initialize(ECGenParameterSpec(EC_CURVE_NAME))
        val keyPair = generator.generateKeyPair()
        val privateKey = Base64.encodeToString(keyPair.private.encoded, base64Flag)
        val publicKey = Base64.encodeToString(keyPair.public.encoded, base64Flag)
        val authKey = ByteArray(16)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(authKey)
        val pushAuthKey = Base64.encodeToString(authKey, base64Flag)
        return CryptoKeys(
            privateKey = privateKey,
            publicKey = publicKey,
            encodedPublicKey = Base64.encodeToString(
                serializeRawPublicKey(keyPair.public),
                base64Flag,
            ),
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
        keys: PushInfo,
        serverPublicKeyEncoded: String,
        encryptionSalt: String,
        contentEncoding: String,
        data: ByteArray,
    ): String {
        val base64Flag = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        val salt = encryptionSalt.decodeBase64()
        val serverKey = deserializeRawPublicKey(serverPublicKeyEncoded.decodeBase64())!!

        val keyFactory = KeyFactory.getInstance("EC")
        val formalPrivateKey = keyFactory.generatePrivate(
            PKCS8EncodedKeySpec(Base64.decode(keys.privateKey, base64Flag))
        )
        val publicKey = keyFactory.generatePublic(
            X509EncodedKeySpec(Base64.decode(keys.publicKey, base64Flag))
        )

        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(formalPrivateKey)
        keyAgreement.doPhase(serverKey, true)
        val sharedSecret = keyAgreement.generateSecret()

        val secondSaltInfo = "Content-Encoding: auth\u0000".toByteArray(StandardCharsets.UTF_8)
        val authKey = Base64.decode(keys.authKey, base64Flag)
        val secondSalt: ByteArray = deriveKey(authKey, sharedSecret, secondSaltInfo, 32)
        val keyInfo: ByteArray = info(contentEncoding, publicKey, serverKey)
        val key = deriveKey(salt, secondSalt, keyInfo, 16)
        val nonceInfo: ByteArray = info("nonce", publicKey, serverKey)
        val nonce = deriveKey(salt, secondSalt, nonceInfo, 12)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val aesKey = SecretKeySpec(key, "AES")
        val iv = GCMParameterSpec(128, nonce)
        cipher.init(Cipher.DECRYPT_MODE, aesKey, iv)
        val decrypted = cipher.doFinal(data)
        return String(decrypted, 2, decrypted.size - 2, StandardCharsets.UTF_8)
    }

    private fun String.decodeBase64(): ByteArray {
        return Base64.decode(
            this,
            if (reBase64UrlSafe.containsMatchIn(this)) Base64.URL_SAFE else Base64.DEFAULT,
        )
    }

    private fun deriveKey(
        firstSalt: ByteArray,
        secondSalt: ByteArray,
        info: ByteArray,
        length: Int
    ): ByteArray {
        val hmacContext = Mac.getInstance("HmacSHA256")
        hmacContext.init(SecretKeySpec(firstSalt, "HmacSHA256"))
        val hmac = hmacContext.doFinal(secondSalt)
        hmacContext.init(SecretKeySpec(hmac, "HmacSHA256"))
        hmacContext.update(info)
        val result = hmacContext.doFinal(byteArrayOf(1))
        return if (result.size <= length) result else Arrays.copyOfRange(result, 0, length)
    }

    private fun deserializeRawPublicKey(rawBytes: ByteArray): PublicKey? {
        if (rawBytes.size != 65 && rawBytes.size != 64) return null
        val kf = KeyFactory.getInstance("EC")
        val os = ByteArrayOutputStream()
        os.write(P256_HEAD)
        if (rawBytes.size == 64) os.write(4)
        os.write(rawBytes)
        return kf.generatePublic(X509EncodedKeySpec(os.toByteArray()))
    }

    private fun info(
        type: String,
        clientPublicKey: PublicKey,
        serverPublicKey: PublicKey
    ): ByteArray {
        val info = ByteArrayOutputStream()
        try {
            info.write("Content-Encoding: ".toByteArray(StandardCharsets.UTF_8))
            info.write(type.toByteArray(StandardCharsets.UTF_8))
            info.write(0)
            info.write("P-256".toByteArray(StandardCharsets.UTF_8))
            info.write(0)
            info.write(0)
            info.write(65)
            info.write(serializeRawPublicKey(clientPublicKey))
            info.write(0)
            info.write(65)
            info.write(serializeRawPublicKey(serverPublicKey))
        } catch (ignore: IOException) {
        }
        return info.toByteArray()
    }
}

data class CryptoKeys(
    val privateKey: String,
    val encodedPublicKey: String,
    val publicKey: String,
    val authKey: String,
)
