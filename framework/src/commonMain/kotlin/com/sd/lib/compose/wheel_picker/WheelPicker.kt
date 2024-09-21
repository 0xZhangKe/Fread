package com.sd.lib.compose.wheel_picker

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.Log

interface FWheelPickerContentScope {
    val state: FWheelPickerState
}

interface FWheelPickerDisplayScope : FWheelPickerContentScope {
    @Composable
    fun Content(index: Int)
}

@Composable
fun FVerticalWheelPicker(
    modifier: Modifier = Modifier,
    count: Int,
    state: FWheelPickerState = rememberFWheelPickerState(),
    key: ((index: Int) -> Any)? = null,
    itemHeight: Dp = 35.dp,
    unfocusedCount: Int = 1,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    debug: Boolean = false,
    focus: @Composable () -> Unit = { FWheelPickerFocusVertical() },
    display: @Composable FWheelPickerDisplayScope.(index: Int) -> Unit = { DefaultWheelPickerDisplay(it) },
    content: @Composable FWheelPickerContentScope.(index: Int) -> Unit,
) {
    WheelPicker(
        modifier = modifier,
        isVertical = true,
        count = count,
        state = state,
        key = key,
        itemSize = itemHeight,
        unfocusedCount = unfocusedCount,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        debug = debug,
        focus = focus,
        display = display,
        content = content,
    )
}

@Composable
fun FHorizontalWheelPicker(
    modifier: Modifier = Modifier,
    count: Int,
    state: FWheelPickerState = rememberFWheelPickerState(),
    key: ((index: Int) -> Any)? = null,
    itemWidth: Dp = 35.dp,
    unfocusedCount: Int = 1,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    debug: Boolean = false,
    focus: @Composable () -> Unit = { FWheelPickerFocusHorizontal() },
    display: @Composable FWheelPickerDisplayScope.(index: Int) -> Unit = { DefaultWheelPickerDisplay(it) },
    content: @Composable FWheelPickerContentScope.(index: Int) -> Unit,
) {
    WheelPicker(
        modifier = modifier,
        isVertical = false,
        count = count,
        state = state,
        key = key,
        itemSize = itemWidth,
        unfocusedCount = unfocusedCount,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        debug = debug,
        focus = focus,
        display = display,
        content = content,
    )
}

@Composable
private fun WheelPicker(
    modifier: Modifier,
    isVertical: Boolean,
    count: Int,
    state: FWheelPickerState,
    key: ((index: Int) -> Any)?,
    itemSize: Dp,
    unfocusedCount: Int,
    userScrollEnabled: Boolean,
    reverseLayout: Boolean,
    debug: Boolean,
    focus: @Composable () -> Unit,
    display: @Composable FWheelPickerDisplayScope.(index: Int) -> Unit,
    content: @Composable FWheelPickerContentScope.(index: Int) -> Unit,
) {
    require(count >= 0) { "require count >= 0" }
    require(unfocusedCount >= 0) { "require unfocusedCount >= 0" }

    state.debug = debug
    LaunchedEffect(state, count) {
        state.updateCount(count)
    }

    val nestedScrollConnection = remember(state) {
        WheelPickerNestedScrollConnection(state)
    }.apply {
        this.isVertical = isVertical
        this.itemSizePx = with(LocalDensity.current) { itemSize.roundToPx() }
        this.reverseLayout = reverseLayout
    }

    val totalSize = remember(itemSize, unfocusedCount) {
        itemSize * (unfocusedCount * 2 + 1)
    }

    val displayScope = remember(state) {
        FWheelPickerDisplayScopeImpl(state)
    }.apply {
        this.content = content
    }

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .run {
                if (totalSize > 0.dp) {
                    if (isVertical) {
                        height(totalSize).widthIn(40.dp)
                    } else {
                        width(totalSize).heightIn(40.dp)
                    }
                } else {
                    this
                }
            },
        contentAlignment = Alignment.Center,
    ) {

        val lazyListScope: LazyListScope.() -> Unit = {

            repeat(unfocusedCount) {
                item(contentType = "placeholder") {
                    ItemSizeBox(
                        isVertical = isVertical,
                        itemSize = itemSize,
                    )
                }
            }

            items(
                count = count,
                key = key,
            ) { index ->
                ItemSizeBox(
                    isVertical = isVertical,
                    itemSize = itemSize,
                ) {
                    displayScope.display(index)
                }
            }

            repeat(unfocusedCount) {
                item(contentType = "placeholder") {
                    ItemSizeBox(
                        isVertical = isVertical,
                        itemSize = itemSize,
                    )
                }
            }
        }

        if (isVertical) {
            LazyColumn(
                state = state.lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListScope,
            )
        } else {
            LazyRow(
                state = state.lazyListState,
                verticalAlignment = Alignment.CenterVertically,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListScope,
            )
        }

        ItemSizeBox(
            modifier = Modifier.align(Alignment.Center),
            isVertical = isVertical,
            itemSize = itemSize,
        ) {
            focus()
        }
    }
}

@Composable
private fun ItemSizeBox(
    modifier: Modifier = Modifier,
    isVertical: Boolean,
    itemSize: Dp,
    content: @Composable () -> Unit = { },
) {
    Box(
        modifier
            .run {
                if (isVertical) {
                    height(itemSize)
                } else {
                    width(itemSize)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private class WheelPickerNestedScrollConnection(
    private val state: FWheelPickerState,
) : NestedScrollConnection {
    var isVertical: Boolean = true
    var itemSizePx: Int = 0
    var reverseLayout: Boolean = false

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        state.synchronizeCurrentIndexSnapshot()
        return super.onPostScroll(consumed, available, source)
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val currentIndex = state.synchronizeCurrentIndexSnapshot()
        return if (currentIndex >= 0) {
            available.flingItemCount(
                isVertical = isVertical,
                itemSize = itemSizePx,
                decay = exponentialDecay(2f),
                reverseLayout = reverseLayout,
            ).let { flingItemCount ->
                if (flingItemCount == 0) {
                    state.animateScrollToIndex(currentIndex)
                } else {
                    state.animateScrollToIndex(currentIndex - flingItemCount)
                }
            }
            available
        } else {
            super.onPreFling(available)
        }
    }
}

private fun Velocity.flingItemCount(
    isVertical: Boolean,
    itemSize: Int,
    decay: DecayAnimationSpec<Float>,
    reverseLayout: Boolean,
): Int {
    if (itemSize <= 0) return 0
    val velocity = if (isVertical) y else x
    val targetValue = decay.calculateTargetValue(0f, velocity)
    val flingItemCount = (targetValue / itemSize).toInt()
    return if (reverseLayout) -flingItemCount else flingItemCount
}

private class FWheelPickerDisplayScopeImpl(
    override val state: FWheelPickerState,
) : FWheelPickerDisplayScope {

    var content: @Composable FWheelPickerContentScope.(index: Int) -> Unit by mutableStateOf({})

    @Composable
    override fun Content(index: Int) {
        content(index)
    }
}

internal inline fun logMsg(debug: Boolean, noinline block: () -> String) {
    if (debug) {
        Log.i("FWheelPicker", message = block)
    }
}
