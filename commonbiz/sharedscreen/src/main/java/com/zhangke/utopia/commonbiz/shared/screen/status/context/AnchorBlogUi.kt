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
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.ui.BlogContent
import com.zhangke.utopia.status.ui.BlogDivider
import com.zhangke.utopia.status.ui.StatusInfoLine
import com.zhangke.utopia.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultBlogStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun AnchorBlogUi(
    modifier: Modifier,
    blog: Blog,
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
    reblogAuthor: BlogAuthor? = null,
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
    onUserInfoClick: (BlogAuthor) -> Unit,
    votedOption: (List<BlogPoll.Option>) -> Unit,
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {

            StatusInfoLine(
                modifier = Modifier,
                blogAuthor = blog.author,
                displayTime = displayTime,
                showUpThread = showUpThread,
                style = style,
                moreInteractions = moreInteractions,
                onInteractive = onInteractive,
                reblogAuthor = reblogAuthor,
                onUserInfoClick = onUserInfoClick,
            )
            BlogContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalPadding(style.containerPaddings),
                blog = blog,
                style = style.blogStyle,
                indexOfFeeds = indexInList,
                onMediaClick = onMediaClick,
                votedOption = votedOption,
            )
            StatusBottomInteractionPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalPadding(style.containerPaddings),
                interactions = bottomPanelInteractions,
                onInteractive = onInteractive,
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
