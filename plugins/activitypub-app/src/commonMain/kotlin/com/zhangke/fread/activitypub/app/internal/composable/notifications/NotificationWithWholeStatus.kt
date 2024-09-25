package com.zhangke.fread.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zhangke.fread.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction

@Composable
fun NotificationWithWholeStatus(
    notification: NotificationUiState,
    indexInList: Int,
    icon: ImageVector,
    interactionDesc: String,
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                composedStatusInteraction.onStatusClick(status)
            }
            .padding(vertical = 8.dp)
    ) {
        NotificationHeadLine(
            modifier = Modifier.clickable {
                composedStatusInteraction.onUserInfoClick(notification.role, notification.author)
            },
            icon = icon,
            avatar = notification.account.avatar,
            accountName = notification.account.displayName,
            interactionDesc = interactionDesc,
            style = style,
        )

        WholeBlogUi(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .statusBorder()
                .padding(style.internalBlogPadding),
            statusUiState = status,
            indexInList = indexInList,
            style = style,
            showDivider = false,
            composedStatusInteraction = composedStatusInteraction,
        )
    }
}
