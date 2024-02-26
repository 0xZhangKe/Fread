package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.framework.voyager.pushDestination
import com.zhangke.utopia.activitypub.app.internal.composable.statusInteractive
import com.zhangke.utopia.activitypub.app.internal.screen.user.UserDetailRoute
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.utopia.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.ui.BlogContent
import com.zhangke.utopia.status.ui.BlogUi
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent

@Composable
fun OnlyBlogContentUi(
    modifier: Modifier,
    statusUiState: StatusUiState,
    indexInList: Int,
    style: NotificationStyle,
    votedOption: (List<BlogPoll.Option>) -> Unit,
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
                when (event) {
                    is BlogMediaClickEvent.BlogImageClickEvent -> {
                        transparentNavigator.push(
                            ImageViewerScreen(
                                mediaList = event.mediaList,
                                selectedIndex = event.index,
                                coordinatesList = event.coordinatesList,
                                onDismiss = event.onDismiss,
                            )
                        )
                    }

                    is BlogMediaClickEvent.BlogVideoClickEvent -> {
                        navigator.push(FullVideoScreen(event.media.url.toUri()))
                    }
                }
            },
            votedOption = votedOption,
        )
    }
}

@Composable
fun WholeBlogUi(
    modifier: Modifier,
    statusUiState: StatusUiState,
    indexInList: Int,
    style: NotificationStyle,
    onInteractive: (StatusUiState, StatusUiInteraction) -> Unit,
    votedOption: (List<BlogPoll.Option>) -> Unit,
    showDivider: Boolean = true,
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
            onInteractive = statusInteractive(statusUiState, onInteractive),
            onUserInfoClick = {
                navigator.pushDestination(UserDetailRoute.buildRoute(it.uri))
            },
            onMediaClick = { event ->
                when (event) {
                    is BlogMediaClickEvent.BlogImageClickEvent -> {
                        transparentNavigator.push(
                            ImageViewerScreen(
                                mediaList = event.mediaList,
                                selectedIndex = event.index,
                                coordinatesList = event.coordinatesList,
                                onDismiss = event.onDismiss,
                            )
                        )
                    }

                    is BlogMediaClickEvent.BlogVideoClickEvent -> {
                        navigator.push(FullVideoScreen(event.media.url.toUri()))
                    }
                }
            },
            showDivider = showDivider,
            votedOption = votedOption,
        )
    }
}
