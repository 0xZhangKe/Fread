package com.zhangke.fread.activitypub.app.internal.push

import android.util.Log
import com.zhangke.activitypub.entities.SubscribePushRequestEntity
import com.zhangke.activitypub.entities.SubscriptionAlertsEntity
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@ApplicationScope
actual class PushManager @Inject constructor(
    private val freadConfigManager: FreadConfigManager,
    private val clientManager: ActivityPubClientManager,
    private val pushInfoRepo: PushInfoRepo,
) {

    companion object {

        private const val ENDPOINT_URL = "https://api.fread.xyz/push/relay/send"
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getEndpointUrl(accountId: String): String {
        val deviceId = freadConfigManager.getDeviceId()
        return "$ENDPOINT_URL/$deviceId/${Base64.UrlSafe.encode(accountId.encodeToByteArray())}"
    }

    actual suspend fun subscribe(role: IdentityRole, accountId: String) {
        Log.d("F_TEST", "subscribe for ${role.accountUri}")
        val endpointUrl = getEndpointUrl(accountId)
        val keys = CryptoUtil().generate()
        val subscribeRequest = SubscribePushRequestEntity(
            subscription = SubscribePushRequestEntity.Subscription(
                endpoint = endpointUrl,
                keys = SubscribePushRequestEntity.Subscription.Keys(
                    p256dh = keys.publicKey,
                    auth = keys.authKey,
                ),
            ),
            data = SubscribePushRequestEntity.Data(
                alerts = SubscriptionAlertsEntity(
                    mention = true,
                    status = true,
                    follow = true,
                    reblog = true,
                    followRequest = true,
                    favourite = true,
                    poll = true,
                    update = true,
                ),
                policy = "all",
            ),
        )
        Log.d("F_TEST", "subscribe end point: $endpointUrl")
        Log.d("F_TEST", "subscribe keys : $keys")
        clientManager.getClient(role)
            .pushRepo
            .subscribePush(subscribeRequest)
            .onSuccess {
                val pushInfo = PushInfo(
                    accountId = accountId,
                    publicKey = keys.publicKey,
                    privateKey = keys.privateKey,
                    authKey = keys.authKey,
                )
                pushInfoRepo.insert(pushInfo)
                Log.d("F_TEST", "subscribe success: $it")
            }.onFailure {
                Log.d("F_TEST", "subscribe failed: $it")
            }
    }
}
