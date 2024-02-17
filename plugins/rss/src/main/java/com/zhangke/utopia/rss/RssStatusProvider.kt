package com.zhangke.utopia.rss

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.status.IStatusProvider
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.platform.IPlatformResolver
import com.zhangke.utopia.status.screen.IStatusScreenProvider
import com.zhangke.utopia.status.search.ISearchEngine
import com.zhangke.utopia.status.source.IStatusSourceResolver
import com.zhangke.utopia.status.status.IStatusResolver
import javax.inject.Inject

@Filt
class RssStatusProvider @Inject constructor(
    rssScreenProvider: RssScreenProvider,
    rssSearchEngine: RssSearchEngine,
    rssAccountManager: RssAccountManager,
    rssStatusResolver: RssStatusResolver,
    rssStatusSourceResolver: RssStatusSourceResolver,
    rssPlatformResolver: RssPlatformResolver,
) : IStatusProvider {

    override val screenProvider: IStatusScreenProvider = rssScreenProvider

    override val platformResolver: IPlatformResolver = rssPlatformResolver

    override val searchEngine: ISearchEngine = rssSearchEngine

    override val statusResolver: IStatusResolver = rssStatusResolver

    override val statusSourceResolver: IStatusSourceResolver = rssStatusSourceResolver

    override val accountManager: IAccountManager = rssAccountManager
}
