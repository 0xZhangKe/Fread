package com.zhangke.fread.signal.archive

import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.publish.IPublishBlogManager
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.status.IStatusResolver

class SignalArchiveProvider(
    contentManager: SignalArchiveContentManager,
    signalArchiveScreenProvider: SignalArchiveScreenProvider,
    signalArchiveSearchEngine: SignalArchiveSearchEngine,
    signalArchiveAccountManager: SignalArchiveAccountManager,
    signalArchiveStatusResolver: SignalArchiveStatusResolver,
    signalArchiveStatusSourceResolver: SignalArchiveStatusSourceResolver,
    signalArchiveNotificationResolver: SignalArchiveNotificationResolver,
    signalArchivePublishManager: SignalArchivePublishManager,
) : IStatusProvider {

    override val contentManager: IContentManager = contentManager

    override val screenProvider: IStatusScreenProvider = signalArchiveScreenProvider

    override val searchEngine: ISearchEngine = signalArchiveSearchEngine

    override val statusResolver: IStatusResolver = signalArchiveStatusResolver

    override val statusSourceResolver: IStatusSourceResolver = signalArchiveStatusSourceResolver

    override val accountManager: IAccountManager = signalArchiveAccountManager

    override val notificationResolver: INotificationResolver = signalArchiveNotificationResolver

    override val publishManager: IPublishBlogManager = signalArchivePublishManager
}
