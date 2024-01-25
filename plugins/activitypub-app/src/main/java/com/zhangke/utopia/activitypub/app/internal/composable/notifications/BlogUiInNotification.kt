package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.zhangke.framework.voyager.LocalGlobalNavigator
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.utopia.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.utopia.status.ui.BlogUi
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun BlogUiInNotification(
    modifier: Modifier,
    statusUiState: StatusUiState,
    indexInList: Int,
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    val navigator = LocalGlobalNavigator.current
    val transparentNavigator = LocalTransparentNavigator.current
    val blog = statusUiState.status.intrinsicBlog
    BlogUi(
        modifier = modifier,
        blog = blog,
        displayTime = statusUiState.displayTime,
        bottomPanelInteractions = statusUiState.bottomInteractions,
        moreInteractions = statusUiState.moreInteractions,
        indexInList = indexInList,
        style = defaultStatusStyle(),
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
