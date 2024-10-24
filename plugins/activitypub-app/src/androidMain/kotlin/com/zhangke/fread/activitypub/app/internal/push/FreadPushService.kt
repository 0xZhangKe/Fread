package com.zhangke.fread.activitypub.app.internal.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.framework.architect.json.getLongOrNull
import com.zhangke.framework.architect.json.getStringOrNull
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.activitypub.app.di.activityPubComponent
import com.zhangke.fread.activitypub.app.internal.push.notification.FcmPushMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class FreadPushService : FirebaseMessagingService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("F_TEST", "onNewToken: $token")
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("F_TEST", "onMessageReceived: $message")
        val encodedAccountId = message.data["a"] ?: return
        val messageData = message.data["d"] ?: return
        val serverPublicKeyEncoded =
            message.data["Crypto-Key"]?.let { parseDhFromServerKey(it) } ?: return
        val contentEncoding = message.data["Content-Encoding"] ?: return
        val encryptionSalt = message.data["Encryption"]?.split("=")?.lastOrNull() ?: return
        val accountId = String(Base64.UrlSafe.decode(encodedAccountId))
        coroutineScope.launch {
            val info = getPushRepo().getPushInfo(accountId) ?: return@launch
            val pushMessage = try {
                CryptoUtil.decryptData(
                    keys = info,
                    serverPublicKeyEncoded = serverPublicKeyEncoded,
                    data = Base64.decode(messageData),
                    encryptionSalt = encryptionSalt,
                    contentEncoding = contentEncoding,
                ).let { convertDataToNotification(it) }
            } catch (e: Throwable) {
                Log.d("F_TEST", "decrypted data error: ${e.stackTraceToString()}")
                null
            }
            Log.d("F_TEST", "pushMessage: $pushMessage")
            if (pushMessage != null) {
                activityPubComponent.provideNotificationManager()
                    .onReceiveNewMessage(this@FreadPushService, pushMessage)
            }
        }
    }

    private fun convertDataToNotification(data: String): FcmPushMessage? {
        val jsonObject = globalJson.fromJson<JsonObject>(data)
        return FcmPushMessage(
            accessToken = jsonObject.getStringOrNull("access_token"),
            preferredLocale = jsonObject.getStringOrNull("preferred_locale"),
            notificationId = jsonObject.getLongOrNull("notification_id"),
            notificationType = jsonObject.getStringOrNull("notification_type")
                ?.let { FcmPushMessage.Type.fromName(it) }
                ?: return null,
            icon = jsonObject.getStringOrNull("icon") ?: return null,
            title = jsonObject.getStringOrNull("title") ?: return null,
            body = jsonObject.getStringOrNull("body") ?: return null,
        )
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
