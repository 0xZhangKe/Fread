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
    when (status) {
        is Status.Reblog -> {
            ReblogUi(
                modifier = modifier,
                reblog = status,
                indexInList = indexInList,
                onMediaClick = onMediaClick,
            )
        }

        is Status.NewBlog -> {
            BlogContentUi(
                modifier = modifier,
                blog = status.blog,
                supportActions = status.supportActions,
                indexInList = indexInList,
                onMediaClick = onMediaClick,
            )
        }
    }
}
