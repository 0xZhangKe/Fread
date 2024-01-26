package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState

@Composable
fun FavouriteNotification(
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle,
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
        author = notification.account,
        icon = Icons.Default.Star,
        interactionDesc = stringResource(R.string.activity_pub_notification_favourited_desc),
        indexInList = indexInList,
        style = style,
    )
}
