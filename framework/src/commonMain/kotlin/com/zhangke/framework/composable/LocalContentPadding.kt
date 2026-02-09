package com.zhangke.framework.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalContentPadding = compositionLocalOf {
    PaddingValues(0.dp)
}

@Composable
fun updateTopPadding(topPadding: Dp): PaddingValues {
    val paddings = LocalContentPadding.current
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = paddings.calculateStartPadding(layoutDirection),
        top = topPadding,
        end = paddings.calculateEndPadding(layoutDirection),
        bottom = paddings.calculateBottomPadding(),
    )
}

@Composable
fun plusTopPadding(topPadding: Dp): PaddingValues {
    val paddings = LocalContentPadding.current
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = paddings.calculateStartPadding(layoutDirection),
        top = topPadding + paddings.calculateTopPadding(),
        end = paddings.calculateEndPadding(layoutDirection),
        bottom = paddings.calculateBottomPadding(),
    )
}

@Composable
fun plusContentPadding(paddingValues: PaddingValues): PaddingValues {
    val localPaddingValues = LocalContentPadding.current
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = localPaddingValues.calculateStartPadding(layoutDirection) +
            paddingValues.calculateStartPadding(layoutDirection),
        top = localPaddingValues.calculateTopPadding() + paddingValues.calculateTopPadding(),
        end = localPaddingValues.calculateEndPadding(layoutDirection) +
            paddingValues.calculateEndPadding(layoutDirection),
        bottom = localPaddingValues.calculateBottomPadding() + paddingValues.calculateBottomPadding(),
    )
}

fun Modifier.contentBottomPadding(): Modifier = composed {
    val paddings = LocalContentPadding.current
    this.padding(bottom = paddings.calculateBottomPadding())
}
