package com.zhangke.fread.common.daynight

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.StateFlow


enum class DayNightMode {
    DAY,
    NIGHT,
    FOLLOW_SYSTEM,
    ;

    val isNight: Boolean
        @ReadOnlyComposable
        @Composable
        get() {
            return when (this) {
                DAY -> false
                NIGHT -> true
                FOLLOW_SYSTEM -> isSystemInDarkTheme()
            }
        }
}

expect class DayNightHelper {
    val dayNightModeFlow: StateFlow<DayNightMode>
}

expect class ActivityDayNightHelper {
    fun setMode(mode: DayNightMode)
}

val LocalActivityDayNightHelper =
    staticCompositionLocalOf<ActivityDayNightHelper> { error("No ActivityDayNightHelper provided") }
