package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.BlogContent
import com.zhangke.fread.status.ui.BlogUi
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun OnlyBlogContentUi(
    modifier: Modifier,
    blog: Blog,
    isOwner: Boolean,
    indexInList: Int,
    style: StatusStyle,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onUrlClick: (String) -> Unit,
    onMentionClick: (Mention) -> Unit,
    onMentionDidClick: (String) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    val transparentNavigator = LocalTransparentNavigator.current
    Box(modifier = modifier) {
        BlogContent(
            modifier = Modifier.fillMaxWidth(),
            blog = blog,
            isOwner = isOwner,
            indexOfFeeds = indexInList,
            style = style,
            onMediaClick = { event ->
                onStatusMediaClick(
                    transparentNavigator = transparentNavigator,
                    navigator = navigator,
                    event = event,
                )
            },
            onVoted = onVoted,
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
            onMentionDidClick = onMentionDidClick,
            onUrlClick = onUrlClick,
            onShowOriginalClick = {},
            onBlogClick = {},
        )
    }
}

@Composable
fun WholeBlogUi(
    modifier: Modifier,
    statusUiState: StatusUiState,
    indexInList: Int,
    style: StatusStyle,
    showDivider: Boolean = true,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val navigator = LocalNavigator.currentOrThrow
    val transparentNavigator = LocalTransparentNavigator.current
    val blog = statusUiState.status.intrinsicBlog
    Box(modifier = modifier) {
        BlogUi(
            modifier = Modifier,
            blog = blog,
            logged = statusUiState.logged,
            isOwner = statusUiState.isOwner,
            blogTranslationState = statusUiState.blogTranslationState,
            indexInList = indexInList,
            following = statusUiState.following,
            style = style,
            onInteractive = { type, _ ->
                composedStatusInteraction.onStatusInteractive(statusUiState, type)
            },
            onUserInfoClick = {
                composedStatusInteraction.onUserInfoClick(statusUiState.role, it)
            },
            onMediaClick = { event ->
                onStatusMediaClick(
                    transparentNavigator = transparentNavigator,
                    navigator = navigator,
                    event = event,
                )
            },
            showDivider = showDivider,
            onVoted = {
                composedStatusInteraction.onVoted(statusUiState, it)
            },
            onHashtagInStatusClick = {
                composedStatusInteraction.onHashtagInStatusClick(statusUiState.role, it)
            },
            onMentionClick = {
                composedStatusInteraction.onMentionClick(statusUiState.role, it)
            },
            onMentionDidClick = {
                composedStatusInteraction.onMentionClick(
                    role = statusUiState.role,
                    did = it,
                    protocol = statusUiState.status.platform.protocol,
                )
            },
            onUrlClick = {
                browserLauncher.launchWebTabInApp(it, statusUiState.role)
            },
            onShowOriginalClick = {
                composedStatusInteraction.onShowOriginalClick(statusUiState)
            },
            onTranslateClick = {
                composedStatusInteraction.onTranslateClick(statusUiState.role, statusUiState)
            },
            onBlogClick = {
                composedStatusInteraction.onBlockClick(statusUiState.role, it)
            },
        )
    }
}
