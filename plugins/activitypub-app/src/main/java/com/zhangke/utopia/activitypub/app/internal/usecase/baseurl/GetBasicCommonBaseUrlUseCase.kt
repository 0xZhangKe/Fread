package com.zhangke.utopia.activitypub.app.internal.usecase.baseurl

import com.zhangke.framework.network.FormalBaseUrl
import javax.inject.Inject

class GetBasicCommonBaseUrlUseCase @Inject constructor() {

    companion object {

        private val DEFAULT_BASE_URL = FormalBaseUrl.parse("https://mastodon.online")!!
    }

    operator fun invoke(): FormalBaseUrl {
        return DEFAULT_BASE_URL
    }
}
