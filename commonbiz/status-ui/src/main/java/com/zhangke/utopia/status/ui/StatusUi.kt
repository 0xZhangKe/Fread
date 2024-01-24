package com.zhangke.utopia.status.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun StatusUi(
    modifier: Modifier = Modifier,
    status: StatusUiState,
    indexInList: Int,
    style: StatusStyle = defaultStatusStyle(),
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
) {
    Surface(
        modifier = modifier,
    ) {
        when (val rawStatus = status.status) {
            is Status.Reblog -> {
                ReblogUi(
                    modifier = Modifier,
                    reblog = rawStatus,
                    displayTime = status.displayTime,
                    indexInList = indexInList,
                    bottomPanelInteractions = status.bottomInteractions,
                    moreInteractions = status.moreInteractions,
                    onInteractive = onInteractive,
                    style = style,
                    onMediaClick = onMediaClick,
                )
            }

            is Status.NewBlog -> {
                BlogUi(
                    modifier = Modifier,
                    blog = rawStatus.blog,
                    displayTime = status.displayTime,
                    bottomPanelInteractions = status.bottomInteractions,
                    moreInteractions = status.moreInteractions,
                    indexInList = indexInList,
                    style = style,
                    onInteractive = onInteractive,
                    onMediaClick = onMediaClick,
                )
            }
        }
    }
}
