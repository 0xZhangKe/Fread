package com.zhangke.fread.common.daynight

import kotlinx.coroutines.flow.StateFlow

actual class DayNightHelper {
    actual val dayNightModeFlow: StateFlow<DayNightMode>
        get() = TODO("Not yet implemented")
}

actual class ActivityDayNightHelper {
    actual fun setMode(mode: DayNightMode) {
        TODO("Not yet implemented")
    }
}
