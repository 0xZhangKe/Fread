package com.zhangke.fread.common.daynight

import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Inject

@ActivityScope
actual class ActivityDayNightHelper @Inject constructor() {
    actual fun setMode(mode: DayNightMode) {
        TODO("Not yet implemented")
    }
}

internal actual fun setDefaultNightMode(modeValue: Int) {
    // do nothing
}

internal actual val DayNightMode.modeValue: Int
    get() = when (this) {
        DayNightMode.DAY -> 1
        DayNightMode.NIGHT -> 2
        DayNightMode.FOLLOW_SYSTEM -> -100
    }