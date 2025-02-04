package com.zhangke.fread.status.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
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
                composedStatusInteraction.onUserInfoClick(status.role, it)
            },
            onVoted = { options ->
                composedStatusInteraction.onVoted(status, options)
            },
            onHashtagInStatusClick = {
                composedStatusInteraction.onHashtagInStatusClick(status.role, it)
            },
            onMentionClick = {
                composedStatusInteraction.onMentionClick(status.role, it)
            },
            onMentionDidClick = {
                composedStatusInteraction.onMentionClick(
                    role = status.role,
                    did = it,
                    protocol = status.status.platform.protocol,
                )
            },
            onFollowClick = {
                composedStatusInteraction.onFollowClick(status.role, it)
            },
            onUrlClick = {
                browserLauncher.launchWebTabInApp(it, status.role)
            },
            onBoostedClick = {
                composedStatusInteraction.onBoostedClick(status.role, status)
            },
            onFavouritedClick = {
                composedStatusInteraction.onFavouritedClick(status.role, status)
            },
            onShowOriginalClick = {
                composedStatusInteraction.onShowOriginalClick(status)
            },
            onTranslateClick = {
                composedStatusInteraction.onTranslateClick(status.role, status)
            },
            onBlogClick = {
                composedStatusInteraction.onBlockClick(status.role, it)
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
                    composedStatusInteraction.onUserInfoClick(statusUiState.role, it)
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
