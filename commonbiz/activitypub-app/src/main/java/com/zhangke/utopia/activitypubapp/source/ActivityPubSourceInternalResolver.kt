package com.zhangke.utopia.activitypubapp.source

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.blogprovider.BlogSource

internal interface ActivityPubSourceInternalResolver {

    suspend fun resolve(url: ActivityPubUrl, instance: ActivityPubInstance): BlogSource?
}