package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole

@Composable
fun NotificationWithWholeStatus(
    role: IdentityRole,
    notification: NotificationUiState,
    indexInList: Int,
    icon: ImageVector,
    interactionDesc: String,
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigator.push(StatusContextScreen(role, status.status))
            }
            .padding(vertical = 8.dp)
    ) {
        NotificationHeadLine(
            icon = icon,
            avatar = notification.account.avatar,
            accountName = notification.account.displayName,
            interactionDesc = interactionDesc,
            style = style,
        )

        WholeBlogUi(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .statusBorder()
                .padding(style.internalBlogPadding),
            role = role,
            statusUiState = status,
            indexInList = indexInList,
            style = style,
            onInteractive = onInteractive,
            showDivider = false,
            onVoted = onVoted,
        )
    }
}
