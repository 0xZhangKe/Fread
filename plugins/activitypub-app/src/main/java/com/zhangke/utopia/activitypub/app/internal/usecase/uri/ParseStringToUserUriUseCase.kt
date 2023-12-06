package com.zhangke.utopia.activitypub.app.internal.usecase.uri

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ParseStringToUserUriUseCase @Inject constructor() {

    operator fun invoke(uriString: String): ActivityPubUserUri? {
        val uri = StatusProviderUri.from(uriString) ?: return null
        if (uri.host != ActivityPubUri.HOST) return null
        if (uri.path != ActivityPubUserUri.PATH) return null
        val queries = uri.queries
        val webFinger = queries[ActivityPubUserUri.QUERY_FINGER]
            ?.let { WebFinger.create(it) } ?: return null
        val userId = queries[ActivityPubUserUri.QUERY_ID]
        if (userId.isNullOrEmpty()) return null
        return ActivityPubUserUri.create(userId, webFinger)
    }
}
