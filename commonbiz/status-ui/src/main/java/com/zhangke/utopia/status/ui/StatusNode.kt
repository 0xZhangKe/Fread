package com.zhangke.utopia.status.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.status.Status

@Composable
fun StatusNode(
    modifier: Modifier = Modifier,
    status: Status,
    onMediaClick: (BlogMedia) -> Unit,
) {
    val blog = when (status) {
        is Status.NewBlog -> status.blog
    }
    BlogContentComposable(modifier, blog = blog, onMediaClick = onMediaClick)
}
