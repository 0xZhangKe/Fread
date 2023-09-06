package com.zhangke.framework.composable.image.viewer

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import com.zhangke.framework.composable.infinite.InfiniteBox
import com.zhangke.framework.utils.pxToDp
import kotlinx.coroutines.launch

@Composable
fun ImageViewer(
    state: ImageViewerState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    var latestSize: Size? by remember {
        mutableStateOf(null)
    }
    InfiniteBox(
        modifier = modifier
            .onGloballyPositioned { position ->
                val currentSize = position.size.toSize()
                if (currentSize != latestSize) {
                    coroutineScope.launch {
                        state.updateLayoutSize(currentSize)
                    }
                    latestSize = currentSize
                }
            }
            .pointerInput(state) {
                detectTapGestures(
                    onDoubleTap = {
                        if (state.scaled) {
                            coroutineScope.launch {
                                state.animateToStandard()
                            }
                        } else {
                            coroutineScope.launch {
                                state.animateToBig()
                            }
                        }
                    }
                )
            },
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = state.currentOffsetXPixel.pxToDp(density),
                    y = state.currentOffsetYPixel.pxToDp(density)
                )
                .width(state.currentWidthPixel.pxToDp(density))
                .height(state.currentHeightPixel.pxToDp(density))
        ) {
            content()
        }
    }
}
