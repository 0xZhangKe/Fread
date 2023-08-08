package com.zhangke.framework.composable.topout

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.dpToPx
import com.zhangke.framework.utils.pxToDp

@Composable
fun TopOutTopBarLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var topBarHeight by remember {
        mutableStateOf(0.dp)
    }
    var topMargin by remember {
        mutableStateOf(0.dp)
    }

    val finalModifier = if (topBarHeight == 0.dp) {
        Modifier.then(modifier)
    } else {
        val connection = rememberTopOutTopBarLayoutConnection(
            topBarHeight = topBarHeight.dpToPx(density),
        )
        topMargin = connection.topMargin.pxToDp(density)
        Log.d("U_TEST", "topMargin:$topMargin")
        Modifier
            .then(modifier)
            .nestedScroll(connection)
    }
    Box(modifier = finalModifier) {
        Box(
            modifier = Modifier
                .scrollable(rememberScrollState(), Orientation.Vertical)
                .padding(top = topMargin),
        ) {
            content()
        }
        Box(
            modifier = Modifier
                .onGloballyPositioned {
                    if (topBarHeight == 0.dp) {
                        topBarHeight = it.size.height.pxToDp(density)
                    }
                    Log.d("U_TEST", "topBarHeight:$topBarHeight")
                }
                .graphicsLayer(translationY = (-(topBarHeight - topMargin)).dpToPx(density)),
        ) {
            topBar()
        }
        Log.d("U_TEST", "translationY:${(-(topBarHeight - topMargin)).dpToPx(density)}")
    }
}
