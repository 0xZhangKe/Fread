package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Poll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.commonbiz.shared.composable.OnlyBlogContentUi
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_poll_desc
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import org.jetbrains.compose.resources.stringResource

@Composable
fun PollNotification(
    notification: StatusNotification.Poll,
    indexInList: Int,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val role = notification.role
    Column(
        modifier = Modifier
            .clickable {
                composedStatusInteraction.onBlockClick(role, notification.blog)
            }
            .fillMaxWidth()
            .padding(style.containerPaddings),
    ) {
        NotificationHeadLine(
            modifier = Modifier,
            icon = Icons.Default.Poll,
            avatar = null,
            accountName = null,
            interactionDesc = stringResource(Res.string.shared_notification_poll_desc),
            style = style,
        )
        OnlyBlogContentUi(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .fillMaxWidth()
                .statusBorder()
                .padding(style.internalBlogPadding),
            blog = notification.blog,
            isOwner = false,
            indexInList = indexInList,
            style = style.statusStyle,
            onVoted = {},
            onMentionClick = { composedStatusInteraction.onMentionClick(role, it) },
            onMentionDidClick = {
                composedStatusInteraction.onMentionClick(
                    role = role,
                    did = it,
                    protocol = notification.blog.platform.protocol,
                )
            },
            onHashtagInStatusClick = { composedStatusInteraction.onHashtagInStatusClick(role, it) },
            onUrlClick = { browserLauncher.launchWebTabInApp(it, role) },
        )
    }
}
