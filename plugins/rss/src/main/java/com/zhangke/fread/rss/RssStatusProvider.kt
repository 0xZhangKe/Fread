package com.zhangke.fread.rss

import com.zhangke.filt.annotaions.Filt
import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.status.IStatusResolver
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
