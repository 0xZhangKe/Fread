package com.zhangke.fread.activitypub.app.internal.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zhangke.fread.activitypub.app.di.activityPubComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class FreadPushService : FirebaseMessagingService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        Log.d("F_TEST", "init")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("F_TEST", "onCreate")
    }

    override fun onTimeout(startId: Int) {
        super.onTimeout(startId)
        Log.d("F_TEST", "onTimeout: $startId")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("F_TEST", "onNewToken: $token")
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("F_TEST", "onMessageReceived: $message")
        message.data.entries.forEach {
            Log.d("F_TEST", "onMessageReceived: ${it.key}: ${it.value}")
        }
        val encodedAccountId = message.data["a"] ?: return
        val messageData = message.data["d"] ?: return
        val serverPublicKeyEncoded = message.data["Crypto-Key"]?.let { parseDhFromServerKey(it) } ?: return
        val contentEncoding = message.data["Content-Encoding"]
        val encryption = message.data["Encryption"]

        val accountId = String(Base64.UrlSafe.decode(encodedAccountId))
        Log.d("F_TEST", "accountId: $accountId, serverPublicKeyEncoded: $serverPublicKeyEncoded")

        coroutineScope.launch {
            val info = getPushRepo().getPushInfo(accountId) ?: return@launch
            Log.d("F_TEST", "start decrypt data: $messageData")
            val decryptData = try {
                CryptoUtil.decryptData(
                    privateKey = info.privateKey,
                    serverPublicKeyEncoded = serverPublicKeyEncoded,
                    data = Base64.decode(messageData),
                )
            } catch (e: Throwable) {
                Log.d("F_TEST", "decrypted data error: $e")
            }
            Log.d("F_TEST", "decrypted data: $decryptData")
        }
    }

    private fun getPushRepo(): PushInfoRepo {
        val activityPubComponent = activityPubComponent
        val database = activityPubComponent.provideActivityPushDatabase(this)
        return activityPubComponent.providePushInfoRepo(database)
    }

    private fun parseDhFromServerKey(cryptoKey: String): String? {
        return cryptoKey.split(";")
            .firstOrNull()
            ?.takeIf { it.startsWith("dh=") }
            ?.removePrefix("dh=")
    }
}
