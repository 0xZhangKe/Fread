package com.zhangke.utopia.commonbiz.shared.screen.status.context

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.horizontalPadding
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.ui.BlogContent
import com.zhangke.utopia.status.ui.BlogDivider
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
import com.zhangke.utopia.status.ui.StatusInfoLine
import com.zhangke.utopia.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun DescendantStatusUi(
    modifier: Modifier,
    status: StatusUiState,
    displayTime: String,
    style: StatusStyle = defaultStatusStyle(),
    indexInList: Int,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    reblogAuthor: BlogAuthor? = null,
    composedStatusInteraction: ComposedStatusInteraction,
    onMediaClick: OnBlogMediaClick,
) {
    val blog = status.status.intrinsicBlog
    Surface(
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            StatusInfoLine(
                modifier = Modifier,
                blogAuthor = blog.author,
                displayTime = displayTime,
                style = style,
                moreInteractions = moreInteractions,
                onInteractive = {
                    composedStatusInteraction.onStatusInteractive(status.status, it)
                },
                reblogAuthor = reblogAuthor,
                onUserInfoClick = composedStatusInteraction::onUserInfoClick,
            )
            BlogContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalPadding(style.containerPaddings),
                blog = blog,
                style = style.blogStyle,
                indexOfFeeds = indexInList,
                onMediaClick = onMediaClick,
                onVoted = {
                    composedStatusInteraction.onVoted(status.status, it)
                },
                onHashtagInStatusClick = composedStatusInteraction::onHashtagInStatusClick,
                onMentionClick = composedStatusInteraction::onMentionClick,
            )
            StatusBottomInteractionPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalPadding(style.containerPaddings),
                interactions = bottomPanelInteractions,
                onInteractive = {
                    composedStatusInteraction.onStatusInteractive(status.status, it)
                },
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(style.containerPaddings.calculateBottomPadding())
            )
            BlogDivider()
        }
    }
}
