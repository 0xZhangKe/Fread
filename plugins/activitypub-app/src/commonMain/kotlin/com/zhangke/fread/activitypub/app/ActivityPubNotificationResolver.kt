package com.zhangke.fread.activitypub.app

import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class ActivityPubNotificationResolver @Inject constructor(): INotificationResolver {

    override suspend fun getNotifications(
        platform: BlogPlatform,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?,
    ): Result<Pair<String?, List<StatusNotification>>>? {
        TODO("Not yet implemented")
    }
}
