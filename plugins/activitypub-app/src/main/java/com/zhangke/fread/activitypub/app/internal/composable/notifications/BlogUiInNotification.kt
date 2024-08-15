package com.zhangke.fread.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.commonbiz.shared.composable.onStatusMediaClick
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.ui.BlogContent
import com.zhangke.fread.status.ui.BlogUi
import com.zhangke.fread.status.ui.ComposedStatusInteraction

@Composable
fun OnlyBlogContentUi(
    modifier: Modifier,
    statusUiState: StatusUiState,
    indexInList: Int,
    style: NotificationStyle,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onUrlClick: (String) -> Unit,
    onMentionClick: (Mention) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    val transparentNavigator = LocalTransparentNavigator.current
    val blog = statusUiState.status.intrinsicBlog
    Box(modifier = modifier) {
        BlogContent(
            modifier = Modifier.fillMaxWidth(),
            blog = blog,
            blogTranslationState = statusUiState.blogTranslationState,
            indexOfFeeds = indexInList,
            style = style.statusStyle,
            specificTime = statusUiState.specificTime,
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
            onUrlClick = onUrlClick,
        )
    }
}

@Composable
fun WholeBlogUi(
    modifier: Modifier,
    statusUiState: StatusUiState,
    indexInList: Int,
    style: NotificationStyle,
    showDivider: Boolean = true,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow
    val transparentNavigator = LocalTransparentNavigator.current
    val blog = statusUiState.status.intrinsicBlog
    Box(modifier = modifier) {
        BlogUi(
            modifier = Modifier,
            blog = blog,
            blogTranslationState = statusUiState.blogTranslationState,
            indexInList = indexInList,
            displayTime = statusUiState.displayTime,
            specificTime = statusUiState.specificTime,
            editedTime = statusUiState.editedTime,
            following = statusUiState.following,
            bottomPanelInteractions = statusUiState.bottomInteractions,
            moreInteractions = statusUiState.moreInteractions,
            style = style.statusStyle,
            onInteractive = {
                composedStatusInteraction.onStatusInteractive(statusUiState, it)
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
            onUrlClick = {
                BrowserLauncher.launchWebTabInApp(context, it, statusUiState.role)
            },
        )
    }
}
