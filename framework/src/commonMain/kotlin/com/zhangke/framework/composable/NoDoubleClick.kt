package com.zhangke.framework.composable

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private var latestClickTime = 0L
private var intervalThreshold = 700L

@OptIn(ExperimentalTime::class)
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
            val currentTime = Clock.System.now().toEpochMilliseconds()
            if (currentTime - latestClickTime > intervalThreshold) {
                onClick()
            }
            latestClickTime = currentTime
        },
    )
}
