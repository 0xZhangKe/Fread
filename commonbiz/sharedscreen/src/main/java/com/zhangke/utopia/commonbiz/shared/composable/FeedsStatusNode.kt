package com.zhangke.utopia.commonbiz.shared.composable

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.zhangke.framework.voyager.LocalGlobalNavigator
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.utopia.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.utopia.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.StatusUi
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent

@Composable
fun FeedsStatusNode(
    modifier: Modifier = Modifier,
    status: Status,
    indexInList: Int,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    onInteractive: (Status, StatusUiInteraction) -> Unit,
) {
    val navigator = LocalGlobalNavigator.current
    val transparentNavigator = LocalTransparentNavigator.current
    StatusUi(
        modifier = modifier.clickable {
            navigator.push(StatusContextScreen(status))
        },
        status = status,
        indexInList = indexInList,
        bottomPanelInteractions = bottomPanelInteractions,
        moreInteractions = moreInteractions,
        onInteractive = {
            onInteractive(status, it)
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
        })
}
