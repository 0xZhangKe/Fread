package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.ui.StatusUi
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick

@Composable
fun NotificationWithStatus(
    modifier: Modifier,
    notification: NotificationUiState,
    indexInList: Int,
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
) {
    StatusUi(
        modifier = modifier,
        status = notification.status!!,
        indexInList = indexInList,
        onInteractive = onInteractive,
        onMediaClick = onMediaClick,
    )
}
