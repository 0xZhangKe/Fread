package com.zhangke.framework.composable.topout

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.StateSaver
import com.zhangke.framework.utils.dpToPx
import com.zhangke.framework.utils.pxToDp

@Composable
fun TopOutTopBarLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var topBarHeightState by rememberSaveable(saver = StateSaver.MutableDpSaver) {
        mutableStateOf(0.dp)
    }
    var topMarginState by rememberSaveable(saver = StateSaver.MutableDpSaver) {
        mutableStateOf(0.dp)
    }

    val finalModifier = if (topBarHeightState == 0.dp) {
        Modifier.then(modifier)
    } else {
        val connection = rememberTopOutTopBarLayoutConnection(
            topBarHeight = topBarHeightState.dpToPx(density),
        )
        topMarginState = connection.topMargin.pxToDp(density)
        Modifier
            .then(modifier)
            .nestedScroll(connection)
    }
    Box(modifier = finalModifier) {
        Box(
            modifier = Modifier
                .scrollable(rememberScrollState(), Orientation.Vertical)
                .padding(top = topMarginState),
        ) {
            content()
        }
        Box(
            modifier = Modifier
                .onGloballyPositioned {
                    val heightDp = it.size.height.pxToDp(density)
                    if (heightDp > 0.dp && heightDp != topBarHeightState) {
                        topBarHeightState = heightDp
                    }
                }
                .graphicsLayer(translationY = (-(topBarHeightState - topMarginState)).dpToPx(density)),
        ) {
            topBar()
        }
    }
}
