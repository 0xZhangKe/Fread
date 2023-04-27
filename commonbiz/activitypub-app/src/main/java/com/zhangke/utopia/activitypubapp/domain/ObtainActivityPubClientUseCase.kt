package com.zhangke.utopia.activitypubapp.domain

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import javax.inject.Inject

class ObtainActivityPubClientUseCase @Inject constructor() {

    operator fun invoke(host: String): ActivityPubClient {
        return obtainActivityPubClient(host)
    }
}
