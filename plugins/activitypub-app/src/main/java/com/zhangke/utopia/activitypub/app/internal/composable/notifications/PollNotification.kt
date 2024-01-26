package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState

@Composable
fun PollNotification(
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle,
) {
    val status = notification.status
    if (status == null) {
        UnknownNotification(
            notification = notification,
        )
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(style.containerPaddings)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Poll,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.activity_pub_notification_poll_desc),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBorder()
        ) {
            OnlyBlogContentUi(
                modifier = Modifier.fillMaxWidth(),
                statusUiState = status,
                indexInList = indexInList,
            )
            val poll = status.status.intrinsicBlog.poll
            if (poll != null) {
                val votesCount = poll.votesCount
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(R.string.activity_pub_notification_poll_count, votesCount),
                )
            }
        }
    }
}
