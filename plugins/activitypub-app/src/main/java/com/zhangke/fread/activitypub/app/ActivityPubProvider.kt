package com.zhangke.fread.activitypub.app

import com.zhangke.filt.annotaions.Filt
import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.status.IStatusResolver
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
