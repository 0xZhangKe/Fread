package com.zhangke.utopia.activitypub.app.internal.usecase.baseurl

import javax.inject.Inject

class GetBasicCommonBaseUrlUseCase @Inject constructor() {

    operator fun invoke(): String {
        return "https://mastodon.online"
    }
}
