package com.zhangke.fread.bluesky

import com.zhangke.fread.bluesky.internal.content.BlueskyContentManager
import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.status.IStatusResolver
import me.tatarka.inject.annotations.Inject

class BlueskyProvider @Inject constructor(
    blueskyContentManager: BlueskyContentManager,
    screenProvider: BlueskyScreenProvider,
    platformResolver: BlueskyPlatformResolver,
    searchEngine: BlueskySearchEngine,
    statusResolver: BlueskyStatusResolver,
    statusSourceResolver: BlueskyStatusSourceResolver,
    accountManager: BlueskyAccountManager,
    notificationResolver: BlueskyNotificationResolver,
) : IStatusProvider {

    override val contentManager: IContentManager = blueskyContentManager

    override val screenProvider: IStatusScreenProvider = screenProvider

    override val platformResolver: IPlatformResolver = platformResolver

    override val searchEngine: ISearchEngine = searchEngine

    override val statusResolver: IStatusResolver = statusResolver

    override val statusSourceResolver: IStatusSourceResolver = statusSourceResolver

    override val accountManager: IAccountManager = accountManager

    override val notificationResolver: INotificationResolver = notificationResolver
}
