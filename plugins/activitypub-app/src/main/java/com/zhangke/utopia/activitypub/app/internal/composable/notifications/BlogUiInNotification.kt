package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.composable.onStatusMediaClick
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.ui.BlogContent
import com.zhangke.utopia.status.ui.BlogUi
import com.zhangke.utopia.status.ui.ComposedStatusInteraction

@Composable
fun OnlyBlogContentUi(
    modifier: Modifier,
    statusUiState: StatusUiState,
    indexInList: Int,
    style: NotificationStyle,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMentionClick: (Mention) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    val transparentNavigator = LocalTransparentNavigator.current
    val blog = statusUiState.status.intrinsicBlog
    Box(modifier = modifier) {
        BlogContent(
            modifier = Modifier.fillMaxWidth(),
            blog = blog,
            indexOfFeeds = indexInList,
            style = style.statusStyle.blogStyle,
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
    val navigator = LocalNavigator.currentOrThrow
    val transparentNavigator = LocalTransparentNavigator.current
    val blog = statusUiState.status.intrinsicBlog
    Box(modifier = modifier) {
        BlogUi(
            modifier = Modifier,
            blog = blog,
            indexInList = indexInList,
            displayTime = statusUiState.displayTime,
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
        )
    }
}
