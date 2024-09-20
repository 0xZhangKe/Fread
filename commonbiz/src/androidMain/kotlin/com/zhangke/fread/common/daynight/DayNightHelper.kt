package com.zhangke.fread.common.daynight

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class DayNightHelper @Inject constructor(
    private val localConfigManager: LocalConfigManager,
) {

    companion object {
        private const val DAY_NIGHT_SETTING = "day_night_setting"
    }

    private val _dayNightModeFlow: MutableStateFlow<DayNightMode>
    val dayNightModeFlow: StateFlow<DayNightMode>

    init {
        val modeValue = runBlocking {
            getDayNightModeSetting()
        }
        AppCompatDelegate.setDefaultNightMode(modeValue)

        _dayNightModeFlow = MutableStateFlow(modeValue.toDayNightMode())
        dayNightModeFlow = _dayNightModeFlow.asStateFlow()
    }

    fun setActivityDayNightMode() {
        AppCompatDelegate.setDefaultNightMode(_dayNightModeFlow.value.modeValue)
    }

    suspend fun setMode(mode: DayNightMode) {
        _dayNightModeFlow.value = mode
        localConfigManager.putInt(DAY_NIGHT_SETTING, mode.modeValue)
        AppCompatDelegate.setDefaultNightMode(mode.modeValue)
    }

    private suspend fun getDayNightModeSetting(): Int {
        return localConfigManager.getInt(DAY_NIGHT_SETTING)
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