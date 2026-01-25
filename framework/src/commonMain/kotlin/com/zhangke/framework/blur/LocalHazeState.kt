package com.zhangke.framework.blur

import androidx.compose.runtime.compositionLocalOf
import dev.chrisbanes.haze.HazeState

val LocalHazeState = compositionLocalOf<HazeState?> {
    null
}
