package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.ui.ComposedStatusInteraction

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
        author = notification.account,
        icon = Icons.Default.Star,
        interactionDesc = stringResource(R.string.activity_pub_notification_reblog_desc),
        indexInList = indexInList,
        style = style,
        composedStatusInteraction = composedStatusInteraction,
    )
}
