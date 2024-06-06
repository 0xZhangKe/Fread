package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zhangke.framework.composable.horizontalPadding
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.utils.ShareHelper
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
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMentionClick: (Mention) -> Unit,
    showDivider: Boolean = true,
    showUpThread: Boolean = false,
    textSelectable: Boolean = false,
) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxWidth()) {
        StatusInfoLine(
            modifier = Modifier
                .fillMaxWidth(),
            blogAuthor = blog.author,
            displayTime = displayTime,
            moreInteractions = moreInteractions,
            onInteractive = onInteractive,
            onUserInfoClick = onUserInfoClick,
            style = style,
            showUpThread = showUpThread,
            reblogAuthor = reblogAuthor,
        )
        BlogContent(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalPadding(style.containerPaddings),
            blog = blog,
            indexOfFeeds = indexInList,
            style = style.blogStyle,
            onMediaClick = onMediaClick,
            onVoted = onVoted,
            textSelectable = textSelectable,
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
        )
        StatusBottomInteractionPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = style.iconStartPadding, end = style.iconEndPadding),
            iconAlpha = style.iconAlpha,
            interactions = bottomPanelInteractions,
            onInteractive = {
                if (it is StatusUiInteraction.Share) {
                    ShareHelper.shareUrl(context, blog.url, blog.content)
                    return@StatusBottomInteractionPanel
                }
                onInteractive(it)
            },
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
