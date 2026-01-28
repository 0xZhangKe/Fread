package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.browser.launchWebTabInApp
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.BlogFiltered
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.ContinueThread
import com.zhangke.fread.status.ui.label.ReblogTopLabel
import com.zhangke.fread.status.ui.label.StatusMentionOnlyLabel
import com.zhangke.fread.status.ui.label.StatusPinnedLabel
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.threads.ThreadsType

@Composable
fun StatusUi(
    modifier: Modifier = Modifier,
    status: StatusUiState,
    indexInList: Int,
    sharedElementId: String? = null,
    style: StatusStyle = LocalStatusUiConfig.current.contentStyle,
    onMediaClick: OnBlogMediaClick,
    composedStatusInteraction: ComposedStatusInteraction,
    detailModel: Boolean = false,
    showDivider: Boolean = true,
    threadsType: ThreadsType = ThreadsType.UNSPECIFIED,
    onOpenBlogWithOtherAccountClick: (StatusUiState) -> Unit = {},
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
    val resolvedSharedElementId = sharedElementId ?: rawStatus.id
    var continueThreadHeight: Int? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = modifier) {
        BlogUi(
            modifier = Modifier,
            blog = rawStatus.intrinsicBlog,
            isOwner = status.isOwner,
            logged = status.logged,
            blogTranslationState = status.blogTranslationState,
            continueThreadLabelHeight = continueThreadHeight,
            topLabels = getStatusTopLabel(
                isReblog = rawStatus is Status.Reblog,
                pinned = rawStatus.intrinsicBlog.pinned,
                isReply = rawStatus.intrinsicBlog.isReply,
                author = rawStatus.triggerAuthor,
                style = style,
                threadsType = fixedThreadType,
                mentionOnly = rawStatus.intrinsicBlog.visibility == StatusVisibility.DIRECT,
                onUserInfoClick = {
                    composedStatusInteraction.onUserInfoClick(status.locator, it)
                },
                onContinueThreadHeightChanged = { continueThreadHeight = it }
            ),
            indexInList = indexInList,
            sharedElementId = resolvedSharedElementId,
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
                browserLauncher.launchWebTabInApp(coroutineScope, it, status.locator)
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
            onOpenBlogWithOtherAccountClick = {
                onOpenBlogWithOtherAccountClick(status)
            },
            onUnavailableQuoteClick = {
                composedStatusInteraction.onBlogIdClick(
                    locator = status.locator,
                    platform = status.status.platform,
                    blogId = rawStatus.id,
                )
            },
        )
    }
}

fun getStatusTopLabel(
    style: StatusStyle,
    threadsType: ThreadsType,
    isReblog: Boolean,
    pinned: Boolean,
    isReply: Boolean,
    author: BlogAuthor,
    mentionOnly: Boolean,
    onUserInfoClick: (blogAuthor: BlogAuthor) -> Unit,
    onContinueThreadHeightChanged: (height: Int) -> Unit,
): List<@Composable () -> Unit> {
    val labels = mutableListOf<@Composable () -> Unit>()
    if (isReblog) {
        labels += {
            ReblogTopLabel(
                author = author,
                style = style,
                onAuthorClick = onUserInfoClick,
            )
        }
    } else if (threadsType == ThreadsType.CONTINUED_THREAD && isReply) {
        labels += {
            ContinueThread(style = style, onHeightChanged = onContinueThreadHeightChanged)
        }
    }
    if (mentionOnly) {
        labels += {
            StatusMentionOnlyLabel(
                modifier = Modifier,
                style = style,
            )
        }
    }
    if (pinned) {
        labels += {
            StatusPinnedLabel(
                style = style,
            )
        }
    }
    return labels
}
