package com.zhangke.fread.common.usecase

import com.zhangke.framework.network.FormalBaseUrl
import me.tatarka.inject.annotations.Inject

class GetDefaultBaseUrlUseCase @Inject constructor() {

    companion object {

        private val defaultBaseUrl = FormalBaseUrl.parse("https://mastodon.online")!!
    }

    operator fun invoke(): FormalBaseUrl {
        return defaultBaseUrl
    }
}
