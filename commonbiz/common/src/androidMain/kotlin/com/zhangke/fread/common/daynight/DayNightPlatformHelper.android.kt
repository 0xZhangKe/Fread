package com.zhangke.fread.common.daynight

import androidx.appcompat.app.AppCompatDelegate
import com.zhangke.framework.activity.TopActivityManager

actual class DayNightPlatformHelper {

    actual fun setDefaultMode(modeValue: DayNightMode) {
        AppCompatDelegate.setDefaultNightMode(modeValue.modeValue)
    }

    actual fun setMode(mode: DayNightMode) {
        AppCompatDelegate.setDefaultNightMode(mode.modeValue)
        TopActivityManager.topActiveActivity?.recreate()
    }

    actual fun setAmoledMode(enabled: Boolean) {
        TopActivityManager.topActiveActivity?.recreate()
    }

    private val DayNightMode.modeValue: Int
        get() = when (this) {
            DayNightMode.DAY -> AppCompatDelegate.MODE_NIGHT_NO
            DayNightMode.NIGHT -> AppCompatDelegate.MODE_NIGHT_YES
            DayNightMode.FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

}
