package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.common.utils.ShareHelper
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.StatusMentionOnlyLabel
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.threads.ThreadsType
import com.zhangke.fread.status.ui.threads.drawTopOfAvatarLine
import com.zhangke.fread.status.ui.threads.threads

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
    textSelectable: Boolean = false,
    threadsType: ThreadsType = ThreadsType.NONE,
) {
    val context = LocalContext.current
    val mentionOnly = blog.visibility == StatusVisibility.DIRECT
    var infoToTopSpacing: Float? by remember {
        mutableStateOf(null)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .threads(threadsType, infoToTopSpacing, style)
    ) {
        topLabel?.invoke()
        if (mentionOnly) {
            StatusMentionOnlyLabel(
                modifier = Modifier,
                style = style,
            )
        }
        StatusInfoLine(
            modifier = Modifier
                .padding(top = style.containerTopPadding)
                .fillMaxWidth()
                .let {
                    if (threadsType != ThreadsType.NONE) {
                        it.onGloballyPositioned { coordinates ->
                            infoToTopSpacing = coordinates.positionInParent().y
                        }
                    } else {
                        it
                    }
                },
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
                    start = style.containerStartPadding + style.contentStyle.startPadding,
                    top = style.contentStyle.contentToInfoLineSpacing,
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
                .padding(top = style.bottomPanelStyle.topPadding),
            style = style,
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
