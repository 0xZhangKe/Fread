package com.zhangke.utopia.activitypubapp.source

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.utils.ActivityPubApplicableUrl
import com.zhangke.utopia.blogprovider.BlogSource

internal interface ActivityPubInternalSourceInterpreter {

    suspend fun applicable(
        client: ActivityPubClient,
        url: ActivityPubApplicableUrl,
        instance: ActivityPubInstance
    ): Boolean

    suspend fun createSource(
        client: ActivityPubClient,
        url: ActivityPubApplicableUrl,
        instance: ActivityPubInstance
    ): BlogSource

    suspend fun validate(
        client: ActivityPubClient,
        source: BlogSource
    ): Boolean
}