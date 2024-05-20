package com.zhangke.utopia.commonbiz.shared.screen.status.context

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.ui.BlogUi
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultBlogStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun AnchorBlogUi(
    modifier: Modifier,
    status: StatusUiState,
    displayTime: String,
    indexInList: Int,
    showUpThread: Boolean,
    style: StatusStyle = defaultStatusStyle(
        blogStyle = defaultBlogStyle(
            contentMaxLine = Int.MAX_VALUE,
        ),
    ),
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    composedStatusInteraction: ComposedStatusInteraction,
    onMediaClick: OnBlogMediaClick,
) {
    val blog = status.status.intrinsicBlog
    Surface(modifier = modifier) {
        BlogUi(
            modifier = Modifier,
            blog = blog,
            showUpThread = showUpThread,
            displayTime = displayTime,
            indexInList = indexInList,
            style = style,
            textSelectable = true,
            bottomPanelInteractions = bottomPanelInteractions,
            moreInteractions = moreInteractions,
            onInteractive = {
                composedStatusInteraction.onStatusInteractive(status, it)
            },
            onMediaClick = onMediaClick,
            onUserInfoClick = {
                composedStatusInteraction.onUserInfoClick(status.role, it)
            },
            onVoted = {
                composedStatusInteraction.onVoted(status, it)
            },
            onHashtagInStatusClick = {
                composedStatusInteraction.onHashtagInStatusClick(status.role, it)
            },
            onMentionClick = {
                composedStatusInteraction.onMentionClick(status.role, it)
            },
            showDivider = true,
        )
    }
}
