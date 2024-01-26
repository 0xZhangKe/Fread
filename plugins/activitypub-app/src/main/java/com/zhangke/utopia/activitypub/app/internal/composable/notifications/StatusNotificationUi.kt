package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.ui.BlogDivider

@Composable
fun StatusNotificationUi(
    modifier: Modifier,
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle = defaultNotificationStyle(),
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    Column(modifier = modifier) {
        when (notification.type) {
            StatusNotificationType.FAVOURITE -> {
                FavouriteNotification(
                    notification = notification,
                    indexInList = indexInList,
                    style = style,
                )
            }

            StatusNotificationType.REBLOG -> {
                ReblogNotification(
                    notification = notification,
                    indexInList = indexInList,
                    style = style,
                )
            }

            StatusNotificationType.MENTION -> {
                MentionNotification(
                    notification = notification,
                    indexInList = indexInList,
                    onInteractive = onInteractive,
                )
            }

            StatusNotificationType.POLL -> {
                PollNotification(
                    notification = notification,
                    indexInList = indexInList,
                    style = style,
                )
            }

            else -> {
                UnknownNotification(
                    notification = notification,
                )
            }
        }
        BlogDivider()
    }
}

data class NotificationStyle(
    val containerPaddings: PaddingValues,
)

object NotificationStyleDefaults {

    val startPadding = 8.dp

    val topPadding = 4.dp

    val endPadding = 8.dp

    val bottomPadding = 4.dp
}

@Composable
fun defaultNotificationStyle() = NotificationStyle(
    containerPaddings = PaddingValues(
        start = NotificationStyleDefaults.startPadding,
        top = NotificationStyleDefaults.topPadding,
        end = NotificationStyleDefaults.endPadding,
        bottom = NotificationStyleDefaults.bottomPadding,
    ),
)
