package com.zhangke.utopia.activitypubapp

import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.StatusSourceUri
import com.zhangke.utopia.status_provider.StatusSourceUriResolver

/**
 *
 */
class ActivityPubSourceUriResolver: StatusSourceUriResolver {

    override suspend fun resolve(uri: StatusSourceUri): StatusSource? {
        TODO("Not yet implemented")
    }
}