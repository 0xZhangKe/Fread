package com.zhangke.fread.common.push

import kotlinx.serialization.json.JsonObject

interface IPushManager {

    fun getEndpointUrl(encodedAccountId: String, deviceId: String): String

    suspend fun registerToRelay(accountId: String, deviceId: String): Result<Unit>

    suspend fun unregisterToRelay(accountId: String, deviceId: String): Result<JsonObject>

}

interface PushMessageReceiver {

    fun onReceiveNewMessage(message: PushMessage)
}

data class PushMessage(
    val encodedAccountId: String,
    val messageData: String,
    val cryptoKey: String,
    val contentEncoding: String,
    val encryption: String,
)
