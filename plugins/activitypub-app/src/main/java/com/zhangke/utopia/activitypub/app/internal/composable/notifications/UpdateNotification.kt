package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole

@Composable
fun UpdateNotification(
    role: IdentityRole,
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle,
    onInteractive: (StatusUiState, StatusUiInteraction) -> Unit,
    onVoted: (List<BlogPoll.Option>) -> Unit,
) {
    NotificationWithWholeStatus(
        role = role,
        notification = notification,
        indexInList = indexInList,
        icon = Icons.Default.Edit,
        interactionDesc = stringResource(R.string.activity_pub_notification_update_desc),
        style = style,
        onInteractive = onInteractive,
        onVoted = onVoted,
    )
}
