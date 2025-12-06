package com.zhangke.fread.common.daynight

import com.zhangke.fread.common.di.ActivityScope
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

@ActivityScope
actual class ActivityDayNightHelper @Inject constructor(
    private val dayNightHelper: DayNightHelper,
) {
    actual val dayNightModeFlow: StateFlow<DayNightMode> get() = dayNightHelper.dayNightModeFlow

    actual val amoledModeFlow: StateFlow<Boolean> get() = dayNightHelper.amoledModeFlow

    actual fun setMode(mode: DayNightMode) {
        // dayNightHelper.setMode(mode)
        TODO("Not yet implemented")
    }

    actual fun setAmoledMode(enabled: Boolean) {
        // dayNightHelper.setAmoledMode(enabled)
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