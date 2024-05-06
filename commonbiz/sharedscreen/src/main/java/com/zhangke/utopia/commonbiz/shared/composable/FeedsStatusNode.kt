package com.zhangke.utopia.commonbiz.shared.composable

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
import com.zhangke.utopia.status.ui.StatusUi

@Composable
fun FeedsStatusNode(
    modifier: Modifier = Modifier,
    status: StatusUiState,
    indexInList: Int,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val navigator = LocalNavigator.currentOrThrow
    val transparentNavigator = LocalTransparentNavigator.current
    StatusUi(
        modifier = modifier.clickable {
            composedStatusInteraction.onStatusClick(status.status)
        },
        status = status,
        indexInList = indexInList,
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
