package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.status.ui.ComposedStatusInteraction

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
        interactionDesc = stringResource(R.string.activity_pub_notification_new_status_desc),
        style = style,
        composedStatusInteraction = composedStatusInteraction,
    )
}
