package com.zhangke.fread.status.ui.publish

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.ui.style.StatusStyle.ContentStyle
import com.zhangke.fread.status.ui.style.StatusStyles

data class PublishBlogStyle(
    val avatarSize: Dp,
    val nameStyle: TextStyle,
    val handleStyle: TextStyle,
    val contentStyle: ContentStyle,
)

object PublishBlogStyleDefault {

    @Composable
    fun defaultStyle(): PublishBlogStyle {
        return PublishBlogStyle(
            avatarSize = 46.dp,
            nameStyle = MaterialTheme.typography.titleMedium,
            handleStyle = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            contentStyle = StatusStyles.medium().contentStyle,
        )
    }
}
