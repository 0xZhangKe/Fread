package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.runtime.Composable
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_reblog_desc
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.ic_status_forward
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ReblogNotification(
    notification: StatusUiState,
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
        icon = vectorResource(Res.drawable.ic_status_forward),
        interactionDesc = stringResource(Res.string.shared_notification_reblog_desc),
        indexInList = indexInList,
        style = style,
        composedStatusInteraction = composedStatusInteraction,
    )
}
