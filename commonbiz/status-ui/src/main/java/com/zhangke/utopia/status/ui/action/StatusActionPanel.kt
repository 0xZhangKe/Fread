package com.zhangke.utopia.status.ui.action

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.status.model.StatusAction
import com.zhangke.utopia.statusui.R

@Composable
fun StatusActionPanel(
    modifier: Modifier = Modifier,
    actions: List<StatusAction>,
) {
    // 对于不支持任何操作的 Status 来说，不需要显示底部的 action bar，
    // 额外添加的 Action，例如分享等操作需要用户进入详情页之后在去操作，或者长按呼出菜单。
    if (actions.isEmpty()) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val commentAction = actions.mapFirstOrNull { it as? StatusAction.Comment }
        val forwardAction = actions.mapFirstOrNull { it as? StatusAction.Forward }
        val likeAction = actions.mapFirstOrNull { it as? StatusAction.Like }
        val bookmarkAction = actions.mapFirstOrNull { it as? StatusAction.Bookmark }
        val deleteAction = actions.mapFirstOrNull { it as? StatusAction.Delete }

        val highlightColor = MaterialTheme.colorScheme.primary
        val normalColor = MaterialTheme.colorScheme.onSurface
        Box(modifier = Modifier.weight(1F)) {
            StatusActionIcon(
                imageVector = Icons.Default.Comment,
                enabled = commentAction?.enable == true,
                contentDescription = stringResource(R.string.status_ui_comment),
                tint = normalColor,
                onClick = {},
            )
        }
        Box(modifier = Modifier.weight(1F)) {
            StatusActionIcon(
                imageVector = Icons.Default.SwapHoriz,
                enabled = forwardAction?.enable == true,
                contentDescription = stringResource(R.string.status_ui_forward),
                tint = normalColor,
                onClick = {},
            )
        }
        Box(modifier = Modifier.weight(1F)) {
            StatusActionIcon(
                imageVector = if (likeAction?.liked == true) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Filled.FavoriteBorder
                },
                enabled = likeAction?.enable == true,
                contentDescription = stringResource(R.string.status_ui_like),
                tint = if (likeAction?.liked == true) highlightColor else normalColor,
                onClick = {},
            )
        }
        StatusActionIcon(
            imageVector = Icons.Default.Share,
            enabled = true,
            contentDescription = stringResource(R.string.status_ui_share),
            tint = normalColor,
            onClick = {},
        )
    }
}

@Composable
private fun StatusActionIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    enabled: Boolean,
    contentDescription: String,
    text: String? = null,
    tint: Color,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = tint,
            )
            if (text != null) {
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = text,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
