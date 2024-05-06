package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.status.ui.ComposedStatusInteraction

@Composable
fun MentionNotification(
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
    val navigator = LocalNavigator.currentOrThrow
    WholeBlogUi(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                composedStatusInteraction.onStatusClick(status.status)
            },
        statusUiState = status,
        indexInList = indexInList,
        style = style,
        composedStatusInteraction = composedStatusInteraction,
    )
}
