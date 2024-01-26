package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction

@Composable
fun MentionNotification(
    notification: NotificationUiState,
    indexInList: Int,
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    val status = notification.status
    if (status == null) {
        UnknownNotification(
            notification = notification,
        )
        return
    }
    BlogUi(
        modifier = Modifier.fillMaxWidth(),
        statusUiState = status,
        indexInList = indexInList,
        onInteractive = onInteractive,
    )
}
