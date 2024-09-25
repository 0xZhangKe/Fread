package com.zhangke.fread.activitypub.app.internal.composable.notifications

import androidx.compose.runtime.Composable
import com.zhangke.fread.activitypub.app.activity_pub_notification_reblog_desc
import com.zhangke.fread.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.statusui.ic_status_forward
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ReblogNotification(
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val status = notification.status
    if (status == null) {
        UnknownNotification(
            notification = notification,
        )
        return
    }
    BlogInteractionNotification(
        statusUiState = status,
        author = notification.author,
        icon = vectorResource(com.zhangke.fread.statusui.Res.drawable.ic_status_forward),
        interactionDesc = stringResource(com.zhangke.fread.activitypub.app.Res.string.activity_pub_notification_reblog_desc),
        indexInList = indexInList,
        style = style,
        composedStatusInteraction = composedStatusInteraction,
    )
}
