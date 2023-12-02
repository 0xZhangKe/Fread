package com.zhangke.utopia.status.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick

@Composable
fun StatusUi(
    modifier: Modifier = Modifier,
    status: Status,
    indexInList: Int,
    onMediaClick: OnBlogMediaClick,
) {
    val blog = when (status) {
        is Status.NewBlog -> status.blog
    }
    BlogContentUi(
        modifier = modifier,
        blog = blog,
        indexInList = indexInList,
        onMediaClick = onMediaClick,
    )
}
