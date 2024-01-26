package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction

@Composable
fun UpdateNotification(
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle,
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    NotificationWithWholeStatus(
        notification = notification,
        indexInList = indexInList,
        icon = Icons.Default.Edit,
        interactionDesc = stringResource(R.string.activity_pub_notification_update_desc),
        style = style,
        onInteractive = onInteractive,
    )
}
