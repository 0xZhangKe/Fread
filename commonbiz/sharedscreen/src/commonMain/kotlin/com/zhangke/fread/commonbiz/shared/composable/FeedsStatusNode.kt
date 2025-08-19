package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusUi
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun FeedsStatusNode(
    modifier: Modifier = Modifier,
    status: StatusUiState,
    indexInList: Int,
    composedStatusInteraction: ComposedStatusInteraction,
    showDivider: Boolean = true,
    style: StatusStyle = LocalStatusUiConfig.current.contentStyle,
) {
    val navigator = LocalNavigator.currentOrThrow
    val transparentNavigator = LocalTransparentNavigator.current
    StatusUi(
        modifier = modifier.clickable {
            composedStatusInteraction.onStatusClick(status)
        },
        status = status,
        indexInList = indexInList,
        style = style,
        showDivider = showDivider,
        composedStatusInteraction = composedStatusInteraction,
        onMediaClick = { event ->
            onStatusMediaClick(
                transparentNavigator = transparentNavigator,
                navigator = navigator,
                event = event,
            )
        },
    )
}
