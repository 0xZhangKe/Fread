package com.zhangke.framework.composable

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

private var latestClickTime = 0L
private var intervalThreshold = 700L

fun Modifier.noDoubleClick(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    return clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - latestClickTime > intervalThreshold) {
                onClick()
            }
            latestClickTime = currentTime
        },
    )
}
