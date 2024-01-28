package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor

/**
 * 关于博客的通知UI。
 * 例如你发布的帖子被别人点赞、转发、收藏等。
 */
@Composable
fun BlogInteractionNotification(
    statusUiState: StatusUiState,
    author: ActivityPubAccountEntity,
    icon: ImageVector,
    interactionDesc: String,
    indexInList: Int,
    style: NotificationStyle,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        NotificationHeadLine(
            icon = icon,
            avatar = author.avatar,
            accountName = author.displayName,
            interactionDesc = interactionDesc,
            style = style,
        )
        OnlyBlogContentUi(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .statusBorder()
                .padding(style.internalBlogPadding),
            statusUiState = statusUiState,
            indexInList = indexInList,
            style = style,
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
