package com.zhangke.fread.activitypub.app.internal.push

import android.util.Log
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.framework.architect.json.getLongOrNull
import com.zhangke.framework.architect.json.getStringOrNull
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.activitypub.app.internal.push.notification.ActivityPubPushMessage
import com.zhangke.fread.activitypub.app.internal.push.notification.PushNotificationManager
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.common.push.PushMessage
import com.zhangke.fread.status.account.LoggedAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

actual class ActivityPubPushMessageReceiverHelper : KoinComponent {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val pushNotificationManager: PushNotificationManager by inject()
    private val accountRepo: ActivityPubLoggedAccountRepo by inject()
    private val pushInfoRepo: PushInfoRepo by inject()

    @OptIn(ExperimentalEncodingApi::class)
    actual fun onReceiveNewMessage(message: PushMessage) {
        val accountId = String(Base64.UrlSafe.decode(message.encodedAccountId))
        coroutineScope.launch {
            val info = pushInfoRepo.getPushInfo(accountId) ?: return@launch
            val account =
                accountRepo.queryAll().firstOrNull { it.userId == accountId } ?: return@launch
            val pushMessage = try {
                CryptoUtil.decryptData(
                    keys = info,
                    serverPublicKeyEncoded = message.cryptoKey,
                    data = Base64.decode(message.messageData),
                    encryptionSalt = message.encryption,
                    contentEncoding = message.contentEncoding,
                ).let { convertDataToNotification(it, account) }
            } catch (e: Throwable) {
                Log.d("PushManager", "decrypted data error: ${e.stackTraceToString()}")
                null
            }
            Log.d("PushManager", "pushMessage: $pushMessage")
            if (pushMessage != null) {
                pushNotificationManager.onReceiveNewMessage(appContext, pushMessage)
            }
        }
    }

    private fun convertDataToNotification(
        data: String,
        account: LoggedAccount,
    ): ActivityPubPushMessage? {
        val jsonObject = globalJson.fromJson<JsonObject>(data)
        return ActivityPubPushMessage(
            accessToken = jsonObject.getStringOrNull("access_token"),
            preferredLocale = jsonObject.getStringOrNull("preferred_locale"),
            notificationId = jsonObject.getLongOrNull("notification_id"),
            notificationType = jsonObject.getStringOrNull("notification_type")
                ?.let { ActivityPubPushMessage.Type.fromName(it) }
                ?: return null,
            icon = jsonObject.getStringOrNull("icon") ?: return null,
            title = jsonObject.getStringOrNull("title") ?: return null,
            body = jsonObject.getStringOrNull("body") ?: return null,
            account = account,
        )
    }
}
