package com.zhangke.fread.activitypub.app

import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.status.IStatusResolver
import me.tatarka.inject.annotations.Inject

class ActivityPubProvider @Inject constructor(
    internalContentManager: ActivityPubContentManager,
    internalScreenProvider: ActivityPubScreenProvider,
    internalPlatformResolver: ActivityPubPlatformResolver,
    internalSearchEngine: ActivityPubSearchEngine,
    internalStatusResolver: ActivityPubStatusResolver,
    internalSourceResolver: ActivityPubSourceResolver,
    internalAccountManager: ActivityPubAccountManager,
    notificationResolver: ActivityPubNotificationResolver,
    activityPubPublishManager: ActivityPubPublishManager,
) : IStatusProvider {

    override val contentManager: IContentManager = internalContentManager

    override val screenProvider: IStatusScreenProvider = internalScreenProvider

    override val platformResolver = internalPlatformResolver

    override val searchEngine = internalSearchEngine

    override val statusResolver: IStatusResolver = internalStatusResolver

    override val statusSourceResolver = internalSourceResolver

    override val accountManager: IAccountManager = internalAccountManager

    override val notificationResolver: INotificationResolver = notificationResolver

    override val publishManager = activityPubPublishManager
}
