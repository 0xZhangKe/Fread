package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.model.BlogFiltered
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
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
    threadsType: ThreadsType = ThreadsType.NONE,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    if (status.status.intrinsicBlog.filtered?.firstOrNull()?.action == BlogFiltered.FilterAction.HIDE) {
        Box(modifier = modifier.size(1.dp))
        return
    }
    Surface(modifier = modifier) {
        val rawStatus = status.status
        BlogUi(
            modifier = Modifier,
            blog = rawStatus.intrinsicBlog,
            isOwner = status.isOwner,
            logged = status.logged,
            blogTranslationState = status.blogTranslationState,
            topLabel = getStatusTopLabel(status, style, composedStatusInteraction),
            indexInList = indexInList,
            threadsType = threadsType,
            detailModel = detailModel,
            following = status.following,
            style = if (detailModel) style else style.contentIndentStyle(),
            onInteractive = { type, _ ->
                composedStatusInteraction.onStatusInteractive(status, type)
            },
            showDivider = threadsType != ThreadsType.ANCESTOR && threadsType != ThreadsType.FIRST_ANCESTOR,
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
}

private fun getStatusTopLabel(
    statusUiState: StatusUiState,
    style: StatusStyle,
    composedStatusInteraction: ComposedStatusInteraction,
): (@Composable () -> Unit)? {
    val rawStatus = statusUiState.status
    if (rawStatus is Status.Reblog) {
        return {
            ReblogTopLabel(
                author = rawStatus.author,
                style = style,
                onAuthorClick = {
                    composedStatusInteraction.onUserInfoClick(statusUiState.locator, it)
                },
            )
        }
    }
    if (rawStatus.intrinsicBlog.pinned) {
        return {
            StatusPinnedLabel(
                style = style,
            )
        }
    }
    return null
}
