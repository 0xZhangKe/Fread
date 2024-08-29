package com.zhangke.framework.composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll

fun Modifier.applyNestedScrollConnection(
    nestedScrollConnection: NestedScrollConnection?,
): Modifier {
    if (nestedScrollConnection == null) return this
    return Modifier.nestedScroll(nestedScrollConnection) then this
}
