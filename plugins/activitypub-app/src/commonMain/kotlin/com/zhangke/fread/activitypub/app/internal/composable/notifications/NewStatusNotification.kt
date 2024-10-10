package com.zhangke.fread.activitypub.app.internal.composable.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.runtime.Composable
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_notification_new_status_desc
import com.zhangke.fread.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewStatusNotification(
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    NotificationWithWholeStatus(
        notification = notification,
        indexInList = indexInList,
        icon = Icons.Default.NotificationsNone,
        interactionDesc = stringResource(Res.string.activity_pub_notification_new_status_desc),
        style = style,
        composedStatusInteraction = composedStatusInteraction,
    )
}
