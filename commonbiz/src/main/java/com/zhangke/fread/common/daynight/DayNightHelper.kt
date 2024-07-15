package com.zhangke.fread.common.daynight

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.common.config.LocalConfigManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object DayNightHelper {

    private const val DAY_NIGHT_SETTING = "day_night_setting"

    private val _nightModeFlow = MutableSharedFlow<DayNightMode>()
    val dayNightModeFlow: SharedFlow<DayNightMode> get() = _nightModeFlow

    var dayNightMode: DayNightMode
        private set

    init {
        val modeValue = runBlocking {
            getDayNightModeSetting()
        }
        AppCompatDelegate.setDefaultNightMode(modeValue)
        dayNightMode = modeValue.toDayNightMode()
        ApplicationScope.launch {
            _nightModeFlow.emit(dayNightMode)
        }
    }

    fun setActivityDayNightMode() {
        AppCompatDelegate.setDefaultNightMode(dayNightMode.modeValue)
    }

    fun setMode(mode: DayNightMode) {
        dayNightMode = mode
        ApplicationScope.launch {
            LocalConfigManager.putInt(appContext, DAY_NIGHT_SETTING, mode.modeValue)
        }
        AppCompatDelegate.setDefaultNightMode(mode.modeValue)
        ApplicationScope.launch {
            _nightModeFlow.emit(mode)
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

    FOLLOW_SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

    val isNight: Boolean
        get() {
            return when (this) {
                DAY -> false
                NIGHT -> true
                FOLLOW_SYSTEM -> systemIsNight()
            }
        }

    private fun systemIsNight(): Boolean {
        return appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}