package com.zhangke.framework.composable

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private val ModalBottomSheetPositionalThreshold = 56.dp
private val ModalBottomSheetVelocityThreshold = 125.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberTransientModalBottomSheetState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
    initialValue: SheetValue = SheetValue.Hidden,
    skipHiddenState: Boolean = false,
): SheetState {
    val density = LocalDensity.current
    return remember(
        skipPartiallyExpanded,
        confirmValueChange,
        initialValue,
        skipHiddenState,
        density,
    ) {
        // Avoid rememberSaveable here so SheetValue is never serialized into saved state.
        SheetState(
            skipPartiallyExpanded = skipPartiallyExpanded,
            positionalThreshold = { with(density) { ModalBottomSheetPositionalThreshold.toPx() } },
            velocityThreshold = { with(density) { ModalBottomSheetVelocityThreshold.toPx() } },
            initialValue = initialValue,
            confirmValueChange = confirmValueChange,
            skipHiddenState = skipHiddenState,
        )
    }
}
