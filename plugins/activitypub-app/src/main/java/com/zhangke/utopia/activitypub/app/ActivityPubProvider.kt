package com.zhangke.utopia.activitypub.app

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.status.IStatusProvider
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.screen.IStatusScreenProvider
import com.zhangke.utopia.status.status.IStatusResolver
import javax.inject.Inject

@Filt
class ActivityPubProvider @Inject constructor(
    internalScreenProvider: ActivityPubScreenProvider,
    internalPlatformResolver: ActivityPubPlatformResolver,
    internalSearchEngine: ActivityPubSearchEngine,
    internalStatusResolver: ActivityPubStatusResolver,
    internalSourceResolver: ActivityPubSourceResolver,
    internalAccountManager: ActivityPubAccountManager,
) : IStatusProvider {

    override val screenProvider: IStatusScreenProvider = internalScreenProvider

    override val platformResolver = internalPlatformResolver

    override val searchEngine = internalSearchEngine

    override val statusResolver: IStatusResolver = internalStatusResolver

    override val statusSourceResolver = internalSourceResolver

    override val accountManager: IAccountManager = internalAccountManager
}
