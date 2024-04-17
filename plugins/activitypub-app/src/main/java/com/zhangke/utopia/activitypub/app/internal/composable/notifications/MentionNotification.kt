package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole

@Composable
fun MentionNotification(
    role: IdentityRole,
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle,
    onInteractive: (StatusUiState, StatusUiInteraction) -> Unit,
    onVoted: (List<BlogPoll.Option>) -> Unit,
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
                navigator.push(StatusContextScreen(role, status.status))
            },
        role = role,
        statusUiState = status,
        indexInList = indexInList,
        style = style,
        onInteractive = onInteractive,
        onVoted = onVoted,
    )
}
