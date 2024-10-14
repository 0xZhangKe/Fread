package com.zhangke.fread.feature.message.push

import com.zhangke.fread.common.config.FreadConfigManager
import me.tatarka.inject.annotations.Inject

class PushManager @Inject constructor(
    private val freadConfigManager: FreadConfigManager,
) {

    companion object {

        private const val ENDPOINT_URL = "https://localhost/push/relay/send"
    }

    suspend fun getEndpointUrl(accountId: String): String {
        val deviceId = freadConfigManager.getDeviceId()
        return "$ENDPOINT_URL/$deviceId/$accountId"
    }
}
