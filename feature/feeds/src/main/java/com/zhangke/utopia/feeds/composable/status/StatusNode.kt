package com.zhangke.utopia.feeds.composable.status

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.status.status.Status

@Composable
internal fun StatusNode(
    modifier: Modifier = Modifier,
    status: Status,
) {
    val blog = when (status) {
        is Status.NewBlog -> status.blog
        is Status.Forward -> status.originBlog
        is Status.Comment -> status.originBlog
    }
    BlogContentComposable(modifier, blog = blog)
}
