package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.horizontalPadding
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle

@Composable
fun BlogUi(
    modifier: Modifier,
    blog: Blog,
    displayTime: String,
    indexInList: Int,
    style: StatusStyle,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    reblogAuthor: BlogAuthor? = null,
    composedStatusInteraction: ComposedStatusInteraction,
    showDivider: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StatusInfoLine(
            modifier = Modifier
                .fillMaxWidth(),
            blogAuthor = blog.author,
            displayTime = displayTime,
            moreInteractions = moreInteractions,
            onInteractive = composedStatusInteraction::onInteractive,
            onUserInfoClick = composedStatusInteraction::onUserInfoClick,
            style = style,
            reblogAuthor = reblogAuthor,
        )
        BlogContent(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalPadding(style.containerPaddings),
            blog = blog,
            indexOfFeeds = indexInList,
            style = style.blogStyle,
            onMediaClick = composedStatusInteraction::onMediaClick,
            onVoted = composedStatusInteraction::onVoted,
            onHashtagInStatusClick = composedStatusInteraction::onHashtagInStatusClick,
            onMentionClick = composedStatusInteraction::onMentionClick,
        )
        StatusBottomInteractionPanel(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalPadding(style.containerPaddings),
            interactions = bottomPanelInteractions,
            onInteractive = composedStatusInteraction::onInteractive,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(style.containerPaddings.calculateBottomPadding())
        )
        if (showDivider) {
            BlogDivider()
        }
    }
}
