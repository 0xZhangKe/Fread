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
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.StatusMentionOnlyLabel
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.threads.ThreadsType
import com.zhangke.fread.status.ui.threads.threads

@Composable
fun BlogUi(
    modifier: Modifier,
    blog: Blog,
    blogTranslationState: BlogTranslationUiState,
    isOwner: Boolean?,
    logged: Boolean?,
    indexInList: Int,
    style: StatusStyle,
    topLabel: (@Composable () -> Unit)? = null,
    reblogAuthor: BlogAuthor? = null,
    onInteractive: (StatusActionType, Blog) -> Unit,
    onMediaClick: OnBlogMediaClick,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMaybeHashtagClick: (String) -> Unit,
    onUrlClick: (url: String) -> Unit,
    onMentionClick: (Mention) -> Unit,
    onMentionDidClick: (String) -> Unit,
    onShowOriginalClick: () -> Unit,
    onBlogClick: (Blog) -> Unit,
    onTranslateClick: () -> Unit,
    onBoostedClick: ((String) -> Unit)? = null,
    onFavouritedClick: ((String) -> Unit)? = null,
    onFollowClick: ((BlogAuthor) -> Unit)? = null,
    detailModel: Boolean = false,
    showDivider: Boolean = true,
    showBottomPanel: Boolean = true,
    showMoreOperationIcon: Boolean = true,
    threadsType: ThreadsType = ThreadsType.NONE,
) {
    val textHandler = LocalActivityTextHandler.current
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
        val infoTopPadding = if (topLabel != null || mentionOnly) {
            style.infolineToTopLabelPadding
        } else {
            style.containerTopPadding
        }
        StatusInfoLine(
            modifier = Modifier
                .padding(top = infoTopPadding)
                .fillMaxWidth()
                .let {
                    if (threadsType != ThreadsType.NONE && threadsType != ThreadsType.UNSPECIFIED) {
                        it.onGloballyPositioned { coordinates ->
                            infoToTopSpacing = coordinates.positionInParent().y
                        }
                    } else {
                        it
                    }
                },
            blog = blog,
            blogTranslationState = blogTranslationState,
            displayTime = blog.formattingDisplayTime.formattedTime(),
            visibility = blog.visibility,
            isOwner = isOwner,
            showMoreOperationIcon = showMoreOperationIcon,
            allowToShowFollowButton = isOwner == false && detailModel,
            onInteractive = onInteractive,
            onUserInfoClick = onUserInfoClick,
            onUrlClick = onUrlClick,
            onFollowClick = onFollowClick,
            style = style,
            reblogAuthor = reblogAuthor,
            editedAt = blog.editedAt?.instant,
            onTranslateClick = onTranslateClick,
        )
        BlogContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = style.containerStartPadding + style.contentStyle.startPadding,
                    top = style.contentStyle.contentVerticalSpacing,
                    end = style.containerEndPadding,
                ),
            blog = blog,
            isOwner = isOwner,
            blogTranslationState = blogTranslationState,
            detailModel = detailModel,
            indexOfFeeds = indexInList,
            style = style,
            onMediaClick = onMediaClick,
            onVoted = onVoted,
            onUrlClick = onUrlClick,
            onBoostedClick = onBoostedClick,
            onFavouritedClick = onFavouritedClick,
            editedTime = blog.formattedEditAt,
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
            onMentionDidClick = onMentionDidClick,
            onShowOriginalClick = onShowOriginalClick,
            onBlogClick = onBlogClick,
            onMaybeHashtagClick = onMaybeHashtagClick,
        )
        if (showBottomPanel) {
            StatusBottomInteractionPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = style.containerStartPadding / 2 + style.bottomPanelStyle.startPadding,
                        top = style.contentStyle.contentVerticalSpacing,
                        style.containerEndPadding / 2
                    ),
                style = style,
                blog = blog,
                logged = logged,
                onInteractive = { type, blog ->
                    if (type == StatusActionType.SHARE) {
                        textHandler.shareUrl(blog.link, blog.content)
                        return@StatusBottomInteractionPanel
                    }
                    onInteractive(type, blog)
                },
            )
        }
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
