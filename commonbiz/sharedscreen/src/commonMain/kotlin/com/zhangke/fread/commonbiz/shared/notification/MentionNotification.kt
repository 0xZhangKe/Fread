package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.commonbiz.shared.composable.WholeBlogUi
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.ui.ComposedStatusInteraction

@Composable
fun MentionNotification(
    notification: StatusNotification,
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
    WholeBlogUi(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                composedStatusInteraction.onStatusClick(status)
            },
        statusUiState = status,
        indexInList = indexInList,
        style = style.statusStyle,
        composedStatusInteraction = composedStatusInteraction,
    )
}
