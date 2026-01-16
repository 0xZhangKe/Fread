package com.zhangke.fread.common.daynight

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

enum class DayNightMode(val localKey: Int) {
    DAY(1),
    NIGHT(2),
    FOLLOW_SYSTEM(-1),
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

    companion object {

        fun fromLocalKey(key: Int): DayNightMode {
            return when (key) {
                DAY.localKey -> DAY
                NIGHT.localKey -> NIGHT
                FOLLOW_SYSTEM.localKey -> FOLLOW_SYSTEM
                else -> FOLLOW_SYSTEM
            }
        }
    }
}
