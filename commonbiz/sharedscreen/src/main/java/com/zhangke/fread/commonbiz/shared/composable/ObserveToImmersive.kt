package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.zhangke.framework.composable.ScrollDirection
import com.zhangke.framework.composable.rememberDirectionalLazyListState
import com.zhangke.fread.status.ui.common.LocalMainTabConnection

@Composable
fun ObserveToImmersive(listState: LazyListState) {
    val mainTabConnection = LocalMainTabConnection.current
    val coroutineScope = rememberCoroutineScope()
    ObserveToImmersive(
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
fun ObserveToImmersive(
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
