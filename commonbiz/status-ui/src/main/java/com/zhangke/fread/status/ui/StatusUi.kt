package com.zhangke.fread.status.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.ReblogTopLabel
import com.zhangke.fread.status.ui.label.StatusPinnedLabel
import com.zhangke.fread.status.ui.style.LocalStatusStyle
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.style.StatusStyles
import com.zhangke.fread.status.ui.threads.ThreadsType
import com.zhangke.fread.status.ui.threads.contentIndent

@Composable
fun StatusUi(
    modifier: Modifier = Modifier,
    status: StatusUiState,
    indexInList: Int,
    style: StatusStyle = LocalStatusStyle.current ?: StatusStyles.medium(),
    onMediaClick: OnBlogMediaClick,
    composedStatusInteraction: ComposedStatusInteraction,
    textSelectable: Boolean = false,
    detailModel: Boolean = false,
    threadsType: ThreadsType = ThreadsType.NONE,
) {
    val context = LocalContext.current
    Surface(modifier = modifier) {
        val rawStatus = status.status
        BlogUi(
            modifier = Modifier,
            blog = rawStatus.intrinsicBlog,
            blogTranslationState = status.blogTranslationState,
            topLabel = getStatusTopLabel(status, style, composedStatusInteraction),
            displayTime = status.displayTime,
            specificTime = status.specificTime,
            editedTime = status.editedTime,
            bottomPanelInteractions = status.bottomInteractions,
            moreInteractions = status.moreInteractions,
            indexInList = indexInList,
            threadsType = threadsType,
            detailModel = detailModel,
            following = status.following,
            style = if (threadsType.contentIndent) style.contentIndentStyle() else style,
            onInteractive = {
                composedStatusInteraction.onStatusInteractive(status, it)
            },
            textSelectable = textSelectable,
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
            onFollowClick = {
                composedStatusInteraction.onFollowClick(status.role, it)
            },
            onUrlClick = {
                BrowserLauncher.launchWebTabInApp(context, it, status.role)
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
