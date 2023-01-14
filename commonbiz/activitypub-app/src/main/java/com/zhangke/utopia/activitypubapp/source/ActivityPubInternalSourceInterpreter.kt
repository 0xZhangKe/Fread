package com.zhangke.utopia.activitypubapp.source

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.utils.ActivityPubApplicableUrl
import com.zhangke.utopia.blogprovider.BlogSource

internal interface ActivityPubInternalSourceInterpreter {

    suspend fun applicable(url: ActivityPubApplicableUrl, instance: ActivityPubInstance): Boolean

    suspend fun createSource(url: ActivityPubApplicableUrl, instance: ActivityPubInstance): BlogSource

    suspend fun validate(source: BlogSource): Boolean
}