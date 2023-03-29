package com.zhangke.utopia.composable.status

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.status.Status

@Composable
fun StatusNode(
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