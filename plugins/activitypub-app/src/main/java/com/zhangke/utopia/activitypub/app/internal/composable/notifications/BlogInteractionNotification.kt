package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.ui.BlogAuthorAvatar

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
    showBorder: Boolean = true,
) {
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
                modifier = Modifier.size(16.dp),
                imageVector = icon,
                contentDescription = null,
            )
            BlogAuthorAvatar(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(26.dp),
                imageUrl = author.avatar,
            )
            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alignByBaseline(),
                text = author.name.take(10),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alignByBaseline(),
                text = interactionDesc,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        OnlyBlogContentUi(
            modifier = Modifier
                .padding(top = 4.dp)
                .statusBorder(showBorder)
                .padding(6.dp),
            statusUiState = statusUiState,
            indexInList = indexInList,
        )
    }
}

fun Modifier.statusBorder(show: Boolean = true): Modifier {
    return if (show) {
        this.border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
    } else {
        this
    }
}
