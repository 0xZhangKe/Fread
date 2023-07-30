package com.zhangke.utopia.activitypubapp.uri.server

import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ParseUriToServerUriUseCase @Inject constructor() {

    operator fun invoke(uri: StatusProviderUri): ActivityPubServerUri? {
        if (uri.path != ActivityPubServerUri.PATH) return null
        val serverUrl = uri.queries[ActivityPubServerUri.QUERY_URL]
        if (serverUrl.isNullOrEmpty()) return null
        return ActivityPubServerUri.create(serverUrl)
    }
}
