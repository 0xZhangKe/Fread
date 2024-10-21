package com.zhangke.fread.activitypub.app.internal.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.zhangke.framework.architect.http.sharedHttpClient
import com.zhangke.fread.common.config.FreadConfigManager
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.http.takeFrom
import kotlinx.coroutines.tasks.await
import me.tatarka.inject.annotations.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class FreadPushRelayRepo @Inject constructor(
    private val freadConfigManager: FreadConfigManager,
) {

    companion object {

        private const val RELAY_BASE_URL = "https://api.fread.xyz/push"
    }

    suspend fun registerToRelay(accountId: String): Result<Unit> = runCatching {
        val deviceId = freadConfigManager.getDeviceId()
        val token = FirebaseMessaging.getInstance().token.await()
        Log.d("F_TEST", "registerToRelay: $accountId, $deviceId, $token")
        sharedHttpClient.post {
            url {
                takeFrom("$RELAY_BASE_URL/relay/token")
                parameters.append("accountId", accountId)
                parameters.append("deviceId", deviceId)
                parameters.append("token", token)
            }
        }.body()
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun unregisterToRelay(accountId: String): Result<Unit> = runCatching {
        val deviceId = freadConfigManager.getDeviceId()
        val encodedAccountId = Base64.UrlSafe.encode(accountId.encodeToByteArray())
        sharedHttpClient.delete {
            url {
                takeFrom("$RELAY_BASE_URL/relay/token")
                parameters.append("accountId", encodedAccountId)
                parameters.append("deviceId", deviceId)
            }
        }.body()
    }
}
