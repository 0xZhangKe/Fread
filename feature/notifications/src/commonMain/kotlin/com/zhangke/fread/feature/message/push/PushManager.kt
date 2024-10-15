package com.zhangke.fread.feature.message.push

import com.zhangke.fread.common.config.FreadConfigManager
import me.tatarka.inject.annotations.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class PushManager @Inject constructor(
    private val freadConfigManager: FreadConfigManager,
) {

    companion object {

        private const val ENDPOINT_URL = "https://localhost/push/relay/send"
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getEndpointUrl(accountId: String): String {
        val deviceId = freadConfigManager.getDeviceId()
        return "$ENDPOINT_URL/$deviceId/${Base64.UrlSafe.encode(accountId.encodeToByteArray())}"
    }
}
