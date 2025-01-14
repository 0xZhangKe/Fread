package com.zhangke.fread.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Poll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_notification_poll_desc
import com.zhangke.fread.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import org.jetbrains.compose.resources.stringResource

@Composable
fun PollNotification(
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
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
            interactionDesc = stringResource(Res.string.activity_pub_notification_poll_desc),
            style = style,
        )
        OnlyBlogContentUi(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .fillMaxWidth()
                .statusBorder()
                .padding(style.internalBlogPadding),
            statusUiState = status,
            indexInList = indexInList,
            style = style,
            onVoted = {
                composedStatusInteraction.onVoted(status, it)
            },
            onMentionClick = {
                composedStatusInteraction.onMentionClick(status.role, it)
            },
            onMentionDidClick = {
                composedStatusInteraction.onMentionClick(
                    role = status.role,
                    did = it,
                    protocol = status.status.platform.protocol,
                )
            },
            onHashtagInStatusClick = {
                composedStatusInteraction.onHashtagInStatusClick(status.role, it)
            },
            onUrlClick = {
                browserLauncher.launchWebTabInApp(it, status.role)
            },
        )
    }
}
