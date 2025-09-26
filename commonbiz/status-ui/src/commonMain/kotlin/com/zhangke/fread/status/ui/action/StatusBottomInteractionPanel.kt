package com.zhangke.fread.status.ui.action

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.ui.style.StatusStyle
import kotlinx.coroutines.launch

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
        ForwardActionIcon(
            modifier = Modifier,
            blog = blog,
            style = style,
            logged = logged,
            onInteractive = onInteractive,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForwardActionIcon(
    modifier: Modifier,
    blog: Blog,
    style: StatusStyle,
    logged: Boolean?,
    onInteractive: (StatusActionType, Blog) -> Unit,
) {
    var showForwardDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val highlight = blog.forward.forward == true
    StatusActionIcon(
        modifier = modifier,
        imageVector = forwardIcon(),
        enabled = logged == true && (blog.forward.support || blog.quote.support),
        style = style,
        contentDescription = forwardAlt(),
        text = blog.forward.forwardCount?.countToLabel(),
        highLight = highlight,
        contentAlignment = Alignment.CenterStart,
        onClick = {
            if (!blog.quote.support) {
                onInteractive(StatusActionType.FORWARD, blog)
            } else {
                showForwardDialog = true
            }
        },
    )
    if (showForwardDialog) {
        ModalBottomSheet(
            onDismissRequest = { showForwardDialog = false },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                val contentColor = if (highlight) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    LocalContentColor.current
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                sheetState.hide()
                                showForwardDialog = false
                                onInteractive(StatusActionType.FORWARD, blog)
                            }
                        }
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = forwardIcon(),
                        contentDescription = forwardAlt(),
                        tint = contentColor,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = if (highlight) {
                            unforwardAlt()
                        } else {
                            forwardAlt()
                        },
                        maxLines = 1,
                        color = contentColor,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                sheetState.hide()
                                showForwardDialog = false
                                onInteractive(StatusActionType.QUOTE, blog)
                            }
                        }
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = quoteIcon(),
                        contentDescription = quoteAlt(),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = quoteAlt(),
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
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
