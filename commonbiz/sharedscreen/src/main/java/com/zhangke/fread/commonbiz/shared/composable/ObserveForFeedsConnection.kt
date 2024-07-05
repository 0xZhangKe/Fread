package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.zhangke.framework.composable.ScrollDirection
import com.zhangke.framework.composable.rememberDirectionalLazyListState
import com.zhangke.fread.status.ui.common.LocalMainTabConnection

@Composable
fun ObserveForFeedsConnection(listState: LazyListState) {
    val mainTabConnection = LocalMainTabConnection.current
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(listState, mainTabConnection.scrollToTopFlow) {
        mainTabConnection.scrollToTopFlow
            .collect {
                if (listState.layoutInfo.totalItemsCount > 0) {
                    listState.animateScrollToItem(0)
                }
            }
    }
    LaunchedEffect(listState.isScrollInProgress) {
        mainTabConnection.updateContentScrollInProgress(listState.isScrollInProgress)
    }
    ObserveForImmersive(
        listState = listState,
        onImmersiveEvent = {
            if (it) {
                mainTabConnection.openImmersiveMode(coroutineScope)
            } else {
                mainTabConnection.closeImmersiveMode(coroutineScope)
            }
        }
    )
}

@Composable
fun ObserveForImmersive(
    listState: LazyListState,
    onImmersiveEvent: (immersive: Boolean) -> Unit,
) {
    val directional = rememberDirectionalLazyListState(listState).scrollDirection
    LaunchedEffect(directional) {
        if (directional == ScrollDirection.Down) {
            onImmersiveEvent(true)
        } else if (directional == ScrollDirection.Up) {
            onImmersiveEvent(false)
        }
    }
}
