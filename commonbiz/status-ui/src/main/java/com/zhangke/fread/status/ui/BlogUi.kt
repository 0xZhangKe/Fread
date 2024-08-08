package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.common.utils.ShareHelper
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.StatusMentionOnlyLabel
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun BlogUi(
    modifier: Modifier,
    blog: Blog,
    displayTime: String,
    indexInList: Int,
    style: StatusStyle,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    topLabel: (@Composable () -> Unit)? = null,
    reblogAuthor: BlogAuthor? = null,
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onUrlClick: (url: String) -> Unit,
    onMentionClick: (Mention) -> Unit,
    showDivider: Boolean = true,
    showUpThread: Boolean = false,
    textSelectable: Boolean = false,
) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxWidth()) {
        if (topLabel == null) {
            Spacer(modifier = Modifier.height(style.containerTopPadding / 2))
        } else {
            topLabel()
        }
        StatusMentionOnlyLabel(
            modifier = Modifier,
            visibility = blog.visibility,
            style = style,
        )
        StatusInfoLine(
            modifier = Modifier
                .padding(top = style.containerTopPadding / 2)
                .fillMaxWidth(),
            blogAuthor = blog.author,
            displayTime = displayTime,
            visibility = blog.visibility,
            blogUrl = blog.url,
            moreInteractions = moreInteractions,
            onInteractive = onInteractive,
            onUserInfoClick = onUserInfoClick,
            onUrlClick = onUrlClick,
            style = style,
            reblogAuthor = reblogAuthor,
        )
        BlogContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = style.containerStartPadding,
                    top = style.contentToInfoSpacing,
                    end = style.containerEndPadding,
                ),
            blog = blog,
            indexOfFeeds = indexInList,
            style = style,
            onMediaClick = onMediaClick,
            onVoted = onVoted,
            onUrlClick = onUrlClick,
            textSelectable = textSelectable,
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
        )
        StatusBottomInteractionPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = style.bottomPanelStartPadding,
                    top = style.bottomPanelTopPadding,
                    end = style.iconEndPadding,
                ),
            interactions = bottomPanelInteractions,
            onInteractive = {
                reportStatusInteractionClickEvent(it)
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
                .height(style.containerBottomPadding)
        )
        if (showDivider) {
            BlogDivider()
        }
    }
}
