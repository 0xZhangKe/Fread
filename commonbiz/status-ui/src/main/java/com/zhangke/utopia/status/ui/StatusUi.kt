package com.zhangke.utopia.status.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick

@Composable
fun StatusUi(
    modifier: Modifier = Modifier,
    status: Status,
    indexInList: Int,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
) {
    Surface(
        modifier = modifier,
    ) {
        when (status) {
            is Status.Reblog -> {
                ReblogUi(
                    modifier = Modifier,
                    reblog = status,
                    indexInList = indexInList,
                    bottomPanelInteractions = bottomPanelInteractions,
                    moreInteractions = moreInteractions,
                    onInteractive = onInteractive,
                    onMediaClick = onMediaClick,
                )
            }

            is Status.NewBlog -> {
                BlogUi(
                    modifier = Modifier,
                    blog = status.blog,
                    bottomPanelInteractions = bottomPanelInteractions,
                    moreInteractions = moreInteractions,
                    indexInList = indexInList,
                    onInteractive = onInteractive,
                    onMediaClick = onMediaClick,
                )
            }
        }
    }
}
