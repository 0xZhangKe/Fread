package com.zhangke.utopia.status.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.status.Status
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick

@Composable
fun StatusNode(
    modifier: Modifier = Modifier,
    status: Status,
    onMediaClick: OnBlogMediaClick,
) {
    val blog = when (status) {
        is Status.NewBlog -> status.blog
    }
    BlogContentComposable(modifier, blog = blog, onMediaClick = onMediaClick)
}
