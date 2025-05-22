package com.zhangke.fread.rss

import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.publish.IPublishBlogManager
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.status.IStatusResolver
import me.tatarka.inject.annotations.Inject

class RssStatusProvider @Inject constructor(
    contentManager: RssContentManager,
    rssScreenProvider: RssScreenProvider,
    rssSearchEngine: RssSearchEngine,
    rssAccountManager: RssAccountManager,
    rssStatusResolver: RssStatusResolver,
    rssStatusSourceResolver: RssStatusSourceResolver,
    rssPlatformResolver: RssPlatformResolver,
    rssNotificationResolver: RssNotificationResolver,
    rssPublishManager: RssPublishManager,
) : IStatusProvider {

    override val contentManager: IContentManager = contentManager

    override val screenProvider: IStatusScreenProvider = rssScreenProvider

    override val platformResolver: IPlatformResolver = rssPlatformResolver

    override val searchEngine: ISearchEngine = rssSearchEngine

    override val statusResolver: IStatusResolver = rssStatusResolver

    override val statusSourceResolver: IStatusSourceResolver = rssStatusSourceResolver

    override val accountManager: IAccountManager = rssAccountManager

    override val notificationResolver: INotificationResolver = rssNotificationResolver

    override val publishManager: IPublishBlogManager = rssPublishManager
}
