package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.BlogFiltered
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.ContinueThread
import com.zhangke.fread.status.ui.label.ReblogTopLabel
import com.zhangke.fread.status.ui.label.StatusPinnedLabel
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.threads.ThreadsType

@Composable
fun StatusUi(
    modifier: Modifier = Modifier,
    status: StatusUiState,
    indexInList: Int,
    style: StatusStyle = LocalStatusUiConfig.current.contentStyle,
    onMediaClick: OnBlogMediaClick,
    composedStatusInteraction: ComposedStatusInteraction,
    detailModel: Boolean = false,
    showDivider: Boolean = true,
    threadsType: ThreadsType = ThreadsType.UNSPECIFIED,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    if (status.status.intrinsicBlog.filtered?.firstOrNull()?.action == BlogFiltered.FilterAction.HIDE) {
        Box(modifier = modifier.size(1.dp))
        return
    }
    val fixedThreadType =
        if (threadsType == ThreadsType.UNSPECIFIED && status.status.intrinsicBlog.isReply) {
            ThreadsType.CONTINUED_THREAD
        } else {
            threadsType
        }
    val rawStatus = status.status
    BlogUi(
        modifier = modifier,
        blog = rawStatus.intrinsicBlog,
        isOwner = status.isOwner,
        logged = status.logged,
        blogTranslationState = status.blogTranslationState,
        topLabel = getStatusTopLabel(
            isReblog = rawStatus == Status.Reblog,
            pinned = rawStatus.intrinsicBlog.pinned,
            isReply = rawStatus.intrinsicBlog.isReply,
            author = rawStatus.triggerAuthor,
            style = style,
            threadsType = fixedThreadType,
            onUserInfoClick = {
                composedStatusInteraction.onUserInfoClick(status.locator, it)
            },
        ),
        indexInList = indexInList,
        threadsType = fixedThreadType,
        detailModel = detailModel,
        style = if (detailModel) style else style.contentIndentStyle(),
        onInteractive = { type, _ ->
            composedStatusInteraction.onStatusInteractive(status, type)
        },
        showDivider = showDivider && threadsType != ThreadsType.ANCESTOR && threadsType != ThreadsType.FIRST_ANCESTOR,
        onMediaClick = onMediaClick,
        onUserInfoClick = {
            composedStatusInteraction.onUserInfoClick(status.locator, it)
        },
        onVoted = { options ->
            composedStatusInteraction.onVoted(status, options)
        },
        onHashtagInStatusClick = {
            composedStatusInteraction.onHashtagInStatusClick(status.locator, it)
        },
        onMentionClick = {
            composedStatusInteraction.onMentionClick(status.locator, it)
        },
        onMentionDidClick = {
            composedStatusInteraction.onMentionClick(
                locator = status.locator,
                did = it,
                protocol = status.status.platform.protocol,
            )
        },
        onFollowClick = {
            composedStatusInteraction.onFollowClick(status.locator, it)
        },
        onUrlClick = {
            browserLauncher.launchWebTabInApp(it, status.locator)
        },
        onBoostedClick = {
            composedStatusInteraction.onBoostedClick(status.locator, status)
        },
        onFavouritedClick = {
            composedStatusInteraction.onFavouritedClick(status.locator, status)
        },
        onShowOriginalClick = {
            composedStatusInteraction.onShowOriginalClick(status)
        },
        onTranslateClick = {
            composedStatusInteraction.onTranslateClick(status.locator, status)
        },
        onBlogClick = {
            composedStatusInteraction.onBlockClick(status.locator, it)
        },
        onMaybeHashtagClick = {
            composedStatusInteraction.onMaybeHashtagClick(
                locator = status.locator,
                protocol = status.status.platform.protocol,
                hashtag = it,
            )
        },
    )
}

fun getStatusTopLabel(
    style: StatusStyle,
    threadsType: ThreadsType,
    isReblog: Boolean,
    pinned: Boolean,
    isReply: Boolean,
    author: BlogAuthor,
    onUserInfoClick: (blogAuthor: BlogAuthor) -> Unit,
): (@Composable () -> Unit)? {
    if (isReblog) {
        return {
            ReblogTopLabel(
                author = author,
                style = style,
                onAuthorClick = onUserInfoClick,
            )
        }
    } else if (threadsType == ThreadsType.CONTINUED_THREAD && isReply) {
        return {
            ContinueThread(style = style)
        }
    }
    if (pinned) {
        return {
            StatusPinnedLabel(
                style = style,
            )
        }
    }
    return null
}
