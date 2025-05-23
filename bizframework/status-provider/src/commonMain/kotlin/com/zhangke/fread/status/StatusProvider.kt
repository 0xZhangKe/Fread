package com.zhangke.fread.status

import com.zhangke.fread.status.account.AccountManager
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.content.ContentManager
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.NotificationResolver
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.platform.PlatformResolver
import com.zhangke.fread.status.publish.IPublishBlogManager
import com.zhangke.fread.status.publish.PublishBlogManager
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.screen.StatusScreenProvider
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.search.SearchEngine
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.source.StatusSourceResolver
import com.zhangke.fread.status.status.IStatusResolver
import com.zhangke.fread.status.status.StatusResolver

/**
 * Created by ZhangKe on 2022/12/9.
 */
class StatusProvider constructor(
    providers: Set<IStatusProvider>,
) {

    val contentManager = ContentManager(providers.map { it.contentManager })

    val screenProvider = StatusScreenProvider(providers.map { it.screenProvider })

    val searchEngine = SearchEngine(providers.map { it.searchEngine })

    val platformResolver = PlatformResolver(providers.map { it.platformResolver })

    val statusResolver = StatusResolver(providers.map { it.statusResolver })

    val statusSourceResolver = StatusSourceResolver(providers.map { it.statusSourceResolver })

    val accountManager = AccountManager(providers.map { it.accountManager })

    val notificationResolver = NotificationResolver(providers.map { it.notificationResolver })

    val publishManager = PublishBlogManager(providers.map { it.publishManager })
}

interface IStatusProvider {

    val contentManager: IContentManager

    val screenProvider: IStatusScreenProvider

    val platformResolver: IPlatformResolver

    val searchEngine: ISearchEngine

    val statusResolver: IStatusResolver

    val statusSourceResolver: IStatusSourceResolver

    val accountManager: IAccountManager

    val notificationResolver: INotificationResolver

    val publishManager: IPublishBlogManager
}
