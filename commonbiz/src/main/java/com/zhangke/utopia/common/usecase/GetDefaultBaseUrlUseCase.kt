package com.zhangke.utopia.common.usecase

import com.zhangke.framework.network.FormalBaseUrl
import javax.inject.Inject

class GetDefaultBaseUrlUseCase @Inject constructor() {

    companion object {

        private val defaultBaseUrl = FormalBaseUrl.parse("https://mastodon.online")!!
    }

    operator fun invoke(): FormalBaseUrl {
        return defaultBaseUrl
    }
}
