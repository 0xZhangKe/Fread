package com.zhangke.fread.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.ui.ComposedStatusInteraction

/**
 * 关于博客的通知UI。
 * 例如你发布的帖子被别人点赞、转发、收藏等。
 */
@Composable
fun BlogInteractionNotification(
    statusUiState: StatusUiState,
    author: BlogAuthor,
    icon: ImageVector,
    interactionDesc: String,
    indexInList: Int,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
    iconTint: Color = LocalContentColor.current,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                composedStatusInteraction.onStatusClick(statusUiState)
            }
    ) {
        NotificationHeadLine(
            modifier = Modifier.clickable {
                composedStatusInteraction.onUserInfoClick(statusUiState.role, author)
            },
            icon = icon,
            avatar = author.avatar,
            accountName = author.name,
            interactionDesc = interactionDesc,
            style = style,
            iconTint = iconTint,
        )
        OnlyBlogContentUi(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .statusBorder()
                .padding(style.internalBlogPadding),
            statusUiState = statusUiState,
            indexInList = indexInList,
            style = style,
            onVoted = {
                composedStatusInteraction.onVoted(statusUiState, it)
            },
            onHashtagInStatusClick = {
                composedStatusInteraction.onHashtagInStatusClick(statusUiState.role, it)
            },
            onMentionClick = {
                composedStatusInteraction.onMentionClick(statusUiState.role, it)
            },
            onUrlClick = {
                BrowserLauncher.launchWebTabInApp(context, it, statusUiState.role)
            },
        )
    }
}

@Composable
fun Modifier.statusBorder(show: Boolean = true): Modifier {
    return if (show) {
        this.border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
    } else {
        this
    }
}
