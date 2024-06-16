package com.zhangke.fread.activitypub.app.internal.composable.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.ComposedStatusInteraction

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
        icon = Icons.Default.Star,
        interactionDesc = stringResource(R.string.activity_pub_notification_reblog_desc),
        indexInList = indexInList,
        style = style,
        composedStatusInteraction = composedStatusInteraction,
    )
}
