package com.zhangke.utopia.activitypub.app

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.status.IStatusProvider
import com.zhangke.utopia.status.status.IStatusResolver

@Filt
class ActivityPubProvider(
    internalPlatformResolver: ActivityPubPlatformResolver,
    internalSearchEngine: ActivityPubSearchEngine,
    internalSourceResolver: ActivityPubSourceResolver,
): IStatusProvider {

    override val platformResolver = internalPlatformResolver

    override val searchEngine = internalSearchEngine

    override val statusResolver: IStatusResolver
        get() = TODO("Not yet implemented")

    override val statusSourceResolver = internalSourceResolver
}