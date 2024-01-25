package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import com.zhangke.framework.composable.horizontalPadding
import com.zhangke.framework.voyager.LocalGlobalNavigator
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.utopia.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.utopia.status.ui.BlogUi
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun NotificationWithStatus(
    modifier: Modifier,
    notification: NotificationUiState,
    indexInList: Int,
    style: StatusStyle,
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    Column(modifier = modifier) {
        NotificationHeaderLine(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalPadding(style.containerPaddings),
            notification = notification,
            style = style,
        )
        val status = notification.status
        if (status != null) {
            BlogUiInNotification(
                statusUiState = notification.status,
                indexInList = indexInList,
                onInteractive = onInteractive,
            )
        }
    }
}

@Composable
private fun BlogUiInNotification(
    statusUiState: StatusUiState,
    indexInList: Int,
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    val navigator = LocalGlobalNavigator.current
    val transparentNavigator = LocalTransparentNavigator.current
    val blog = statusUiState.status.intrinsicBlog
    BlogUi(
        modifier = Modifier,
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

@Composable
private fun NotificationHeaderLine(
    modifier: Modifier,
    notification: NotificationUiState,
    style: StatusStyle,
) {
    val actionIcon =
        when (notification.type) {
            StatusNotificationType.STATUS,
            StatusNotificationType.MENTION,
            StatusNotificationType.FAVOURITE,
            StatusNotificationType.POLL,
            StatusNotificationType.UPDATE,
            StatusNotificationType.REBLOG -> {
            }

            else -> {
            }
        }
}
