package com.zhangke.fread.status.ui.action

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun StatusBottomInteractionPanel(
    modifier: Modifier = Modifier,
    style: StatusStyle,
    blog: Blog,
    logged: Boolean?,
    onInteractive: (StatusActionType, Blog) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusActionIcon(
            modifier = Modifier,
            imageVector = forwardIcon(),
            enabled = logged == true && blog.forward.support,
            style = style,
            contentDescription = forwardAlt(),
            text = blog.forward.forwardCount?.countToLabel(),
            highLight = blog.forward.forward == true,
            contentAlignment = Alignment.CenterStart,
            onClick = { onInteractive(StatusActionType.FORWARD, blog) },
        )
        Spacer(modifier = Modifier.weight(1F))
        StatusActionIcon(
            modifier = Modifier,
            imageVector = replyIcon(),
            enabled = logged == true && blog.reply.support,
            style = style,
            contentDescription = replyAlt(),
            text = blog.reply.repliesCount?.countToLabel(),
            highLight = false,
            onClick = { onInteractive(StatusActionType.REPLY, blog) },
        )
        if (blog.quote.support) {
            Spacer(modifier = Modifier.weight(1F))
            StatusActionIcon(
                modifier = Modifier,
                imageVector = quoteIcon(),
                enabled = logged == true && blog.quote.support,
                style = style,
                contentDescription = quoteAlt(),
                text = null,
                highLight = false,
                onClick = { onInteractive(StatusActionType.QUOTE, blog) },
            )
        }
        Spacer(modifier = Modifier.weight(1F))
        StatusActionIcon(
            modifier = Modifier,
            imageVector = likeIcon(blog.like.liked == true),
            enabled = logged == true && blog.like.support,
            style = style,
            contentDescription = likeAlt(),
            text = blog.like.likedCount?.countToLabel(),
            highLight = blog.like.liked == true,
            onClick = { onInteractive(StatusActionType.LIKE, blog) },
        )
        if (blog.bookmark.support) {
            Spacer(modifier = Modifier.weight(1F))
            StatusActionIcon(
                modifier = Modifier,
                imageVector = bookmarkIcon(blog.bookmark.bookmarked == true),
                enabled = logged == true,
                style = style,
                contentDescription = bookmarkAlt(blog.bookmark.bookmarked == true),
                text = null,
                highLight = blog.bookmark.bookmarked == true,
                onClick = { onInteractive(StatusActionType.BOOKMARK, blog) },
            )
        }
        Spacer(modifier = Modifier.weight(1F))
        StatusActionIcon(
            modifier = Modifier,
            imageVector = shareIcon(),
            enabled = true,
            style = style,
            contentDescription = shareAlt(),
            text = null,
            highLight = false,
            contentAlignment = Alignment.CenterEnd,
            onClick = { onInteractive(StatusActionType.SHARE, blog) },
        )
    }
}

@Composable
private fun StatusActionIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    enabled: Boolean,
    contentDescription: String,
    style: StatusStyle,
    text: String? = null,
    highLight: Boolean,
    onClick: () -> Unit,
    contentAlignment: Alignment = Alignment.Center,
) {
    StatusIconButton(
        modifier = modifier.height(style.bottomPanelStyle.iconSize),
        onClick = onClick,
        enabled = enabled,
        contentAlignment = contentAlignment,
    ) {
        Row(
            modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val contentColor = if (highLight) {
                MaterialTheme.colorScheme.tertiary
            } else {
                LocalContentColor.current
            }
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = contentColor,
            )
            if (text != null) {
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = text,
                    maxLines = 1,
                    color = contentColor,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
