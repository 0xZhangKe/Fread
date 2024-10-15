package com.zhangke.fread.feature.message.crypto

expect class CryptoUtil() {

    fun generate(): CryptoKeys
}

data class CryptoKeys(
    val publicKey: String,
    val authKey: String,
)
