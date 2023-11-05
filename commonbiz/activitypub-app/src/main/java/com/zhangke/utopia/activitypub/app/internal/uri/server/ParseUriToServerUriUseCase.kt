package com.zhangke.utopia.activitypub.app.internal.uri.server

import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ParseUriToServerUriUseCase @Inject constructor() {

    operator fun invoke(uri: StatusProviderUri): ActivityPubServerUri? {
        if (uri.path != ActivityPubServerUri.PATH) return null
        val host = uri.queries[ActivityPubServerUri.QUERY_HOST]
        if (host.isNullOrEmpty()) return null
        return ActivityPubServerUri.create(host)
    }
}
