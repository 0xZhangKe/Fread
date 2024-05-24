package com.zhangke.utopia.common.daynight

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.common.config.LocalConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object DayNightHelper {

    private const val DAY_NIGHT_SETTING = "day_night_setting"

    private val _nightModeFlow: MutableStateFlow<Boolean>
    val nightModeFlow: StateFlow<Boolean> get() = _nightModeFlow

    private var dayNightMode: DayNightMode

    init {
        val modeValue = runBlocking {
            getDayNightModeSetting()
        }
        AppCompatDelegate.setDefaultNightMode(modeValue)
        dayNightMode = modeValue.toDayNightMode()
        _nightModeFlow = MutableStateFlow(dayNightMode.isNight())
    }

    fun setActivityDayNightMode() {
        AppCompatDelegate.setDefaultNightMode(dayNightMode.modeValue)
    }

    private fun DayNightMode.isNight(): Boolean {
        return when (this) {
            DayNightMode.DAY -> false
            DayNightMode.NIGHT -> true
            DayNightMode.FOLLOW_SYSTEM -> systemIsNight()
        }
    }

    private fun systemIsNight(): Boolean {
        return appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    fun setMode(mode: DayNightMode) {
        dayNightMode = mode
        ApplicationScope.launch {
            LocalConfigManager.putInt(appContext, DAY_NIGHT_SETTING, mode.modeValue)
        }
        AppCompatDelegate.setDefaultNightMode(mode.modeValue)
        ApplicationScope.launch {
            _nightModeFlow.emit(mode.isNight())
        }
    }

    private suspend fun getDayNightModeSetting(): Int {
        return LocalConfigManager.getInt(appContext, DAY_NIGHT_SETTING)
            ?: DayNightMode.FOLLOW_SYSTEM.modeValue
    }

    private fun Int.toDayNightMode(): DayNightMode {
        return when (this) {
            DayNightMode.DAY.modeValue -> DayNightMode.DAY
            DayNightMode.NIGHT.modeValue -> DayNightMode.NIGHT
            DayNightMode.FOLLOW_SYSTEM.modeValue -> DayNightMode.FOLLOW_SYSTEM
            else -> throw IllegalArgumentException("Illegal $this for DayNightMode")
        }
    }
}

enum class DayNightMode(val modeValue: Int) {

    DAY(AppCompatDelegate.MODE_NIGHT_NO),

    NIGHT(AppCompatDelegate.MODE_NIGHT_YES),

    FOLLOW_SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}