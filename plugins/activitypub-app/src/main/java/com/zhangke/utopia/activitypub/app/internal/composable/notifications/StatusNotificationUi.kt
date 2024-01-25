package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun StatusNotificationUi(
    modifier: Modifier,
    notification: NotificationUiState,
    indexInList: Int,
    style: StatusStyle = defaultStatusStyle(),
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    when (notification.type) {
        StatusNotificationType.STATUS,
        StatusNotificationType.MENTION,
        StatusNotificationType.FAVOURITE,
        StatusNotificationType.POLL,
        StatusNotificationType.UPDATE,
        StatusNotificationType.REBLOG -> {
            NotificationWithStatus(
                modifier = modifier,
                notification = notification,
                indexInList = indexInList,
                style = style,
                onInteractive = onInteractive,
            )
        }
        else -> {

        }
    }
}
