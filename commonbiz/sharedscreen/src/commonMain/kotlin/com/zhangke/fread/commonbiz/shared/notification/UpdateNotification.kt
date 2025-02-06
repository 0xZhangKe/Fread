package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_update_desc
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpdateNotification(
    notification: StatusUiState,
    indexInList: Int,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    NotificationWithWholeStatus(
        notification = notification,
        indexInList = indexInList,
        icon = Icons.Default.Edit,
        interactionDesc = stringResource(Res.string.shared_notification_update_desc),
        style = style,
        composedStatusInteraction = composedStatusInteraction,
    )
}
