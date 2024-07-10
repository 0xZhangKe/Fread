package com.zhangke.fread.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.status.ui.ComposedStatusInteraction

@Composable
fun PollNotification(
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val context = LocalContext.current
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
            .clickable {
                composedStatusInteraction.onStatusClick(status)
            },
    ) {
        NotificationHeadLine(
            modifier = Modifier,
            icon = Icons.Default.Poll,
            avatar = null,
            accountName = null,
            interactionDesc = stringResource(R.string.activity_pub_notification_poll_desc),
            style = style,
        )
        Column(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .fillMaxWidth()
                .statusBorder()
        ) {
            OnlyBlogContentUi(
                modifier = Modifier.padding(style.internalBlogPadding),
                statusUiState = status,
                indexInList = indexInList,
                style = style,
                onVoted = {
                    composedStatusInteraction.onVoted(status, it)
                },
                onMentionClick = {
                    composedStatusInteraction.onMentionClick(status.role, it)
                },
                onHashtagInStatusClick = {
                    composedStatusInteraction.onHashtagInStatusClick(status.role, it)
                },
                onUrlClick = {
                    BrowserLauncher.launchWebTabInApp(context, it, status.role)
                },
            )
            val poll = status.status.intrinsicBlog.poll
            if (poll != null) {
                val votesCount = poll.votesCount
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(
                        R.string.activity_pub_notification_poll_count,
                        votesCount
                    ),
                )
            }
        }
    }
}
