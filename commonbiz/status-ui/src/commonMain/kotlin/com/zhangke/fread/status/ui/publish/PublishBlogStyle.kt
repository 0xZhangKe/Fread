package com.zhangke.fread.status.ui.publish

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.style.StatusStyles

data class PublishBlogStyle(
    val topPadding: Dp,
    val startPadding: Dp,
    val endPadding: Dp,
    val statusStyle: StatusStyle,
)

object PublishBlogStyleDefault {

    @Composable
    fun defaultStyle(): PublishBlogStyle {
        val statusStyle = StatusStyles.medium()
        return PublishBlogStyle(
            topPadding = 24.dp,
            startPadding = 16.dp,
            endPadding = 16.dp,
            statusStyle = statusStyle,
        )
    }
}
