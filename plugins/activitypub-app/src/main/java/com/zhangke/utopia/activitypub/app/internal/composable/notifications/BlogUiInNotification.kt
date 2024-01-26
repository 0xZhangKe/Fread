package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.zhangke.framework.voyager.LocalGlobalNavigator
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.utopia.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.utopia.status.ui.BlogContent
import com.zhangke.utopia.status.ui.BlogUi
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent
import com.zhangke.utopia.status.ui.style.BlogStyle
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultBlogStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun OnlyBlogContentUi(
    modifier: Modifier,
    statusUiState: StatusUiState,
    indexInList: Int,
    style: BlogStyle = defaultBlogStyle(),
) {
    val navigator = LocalGlobalNavigator.current
    val transparentNavigator = LocalTransparentNavigator.current
    val blog = statusUiState.status.intrinsicBlog
    Box(modifier = modifier) {
        BlogContent(
            modifier = Modifier,
            blog = blog,
            indexOfFeeds = indexInList,
            style = style,
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
        )
    }
}

@Composable
fun BlogUi(
    modifier: Modifier,
    statusUiState: StatusUiState,
    indexInList: Int,
    style: StatusStyle = defaultStatusStyle(),
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    val navigator = LocalGlobalNavigator.current
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
            style = style,
            onInteractive = onInteractive,
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
        )
    }
}
