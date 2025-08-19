package com.zhangke.fread.activitypub.app.internal.push

import android.util.Log
import com.zhangke.activitypub.entities.SubscribePushRequestEntity
import com.zhangke.activitypub.entities.SubscriptionAlertsEntity
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.push.IPushManager
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.krouter.KRouter
import me.tatarka.inject.annotations.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@ApplicationScope
actual class ActivityPubPushManager @Inject constructor(
    private val freadConfigManager: FreadConfigManager,
    private val clientManager: ActivityPubClientManager,
    private val pushInfoRepo: PushInfoRepo,
) {

    private val pushManager: IPushManager? by lazy {
        KRouter.getServices<IPushManager>().firstOrNull()
    }

    @OptIn(ExperimentalEncodingApi::class)
    actual suspend fun subscribe(locator: PlatformLocator, accountId: String) {
        Log.d("PushManager", "subscribe for ${locator.accountUri}, $accountId")
        val pushManager = pushManager ?: return
        val deviceId = freadConfigManager.getDeviceId()
        val encodedAccountId = Base64.UrlSafe.encode(accountId.encodeToByteArray())
        val endpointUrl =
            pushManager.getEndpointUrl(encodedAccountId, deviceId)
        val keys = CryptoUtil.generate()
        val subscribeRequest = SubscribePushRequestEntity(
            subscription = SubscribePushRequestEntity.Subscription(
                endpoint = endpointUrl,
                keys = SubscribePushRequestEntity.Subscription.Keys(
                    p256dh = keys.encodedPublicKey,
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
        Log.d("PushManager", "subscribe end point: $endpointUrl")
        Log.d("PushManager", "subscribe keys : $keys")
        clientManager.getClient(locator)
            .pushRepo
            .subscribePush(subscribeRequest)
            .onSuccess {
                Log.d("PushManager", "subscribe success: $it")
                registerRelay(pushManager, deviceId, accountId, encodedAccountId, keys)
            }.onFailure {
                Log.d("PushManager", "subscribe failed: $it")
            }
    }

    actual suspend fun unsubscribe(locator: PlatformLocator, accountId: String) {
        Log.d("PushManager", "unsubscribe for ${locator.accountUri}, $accountId")
        val pushManager = pushManager ?: return
        clientManager.getClient(locator)
            .pushRepo
            .removeSubscription()
            .onSuccess {
                Log.d("PushManager", "unsubscribe success.")
            }.onFailure {
                Log.d("PushManager", "unsubscribe failed: $it")
            }
        unregisterRelay(pushManager, freadConfigManager.getDeviceId(), accountId)
    }

    private suspend fun registerRelay(
        pushManager: IPushManager,
        deviceId: String,
        accountId: String,
        encodedAccountId: String,
        keys: CryptoKeys,
    ) {
        pushManager.registerToRelay(encodedAccountId, deviceId)
            .onSuccess {
                Log.d(
                    "PushManager",
                    "registerRelay success, account id is $accountId, encoded: $encodedAccountId"
                )
                val pushInfo = PushInfo(
                    accountId = accountId,
                    publicKey = keys.publicKey,
                    privateKey = keys.privateKey,
                    authKey = keys.authKey,
                )
                pushInfoRepo.insert(pushInfo)
            }.onFailure {
                Log.d("PushManager", "registerRelay failed: $it")
            }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun unregisterRelay(
        pushManager: IPushManager,
        deviceId: String,
        accountId: String,
    ) {
        Log.d("PushManager", "unregisterRelay: $accountId")
        val encodedAccountId = Base64.UrlSafe.encode(accountId.encodeToByteArray())
        pushManager.unregisterToRelay(encodedAccountId, deviceId)
            .onSuccess {
                Log.d("PushManager", "unregisterRelay success")
            }.onFailure {
                Log.d("PushManager", "unregisterRelay failed: $it")
            }
    }
}
