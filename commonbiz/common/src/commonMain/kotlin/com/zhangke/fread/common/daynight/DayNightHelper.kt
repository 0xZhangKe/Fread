package com.zhangke.fread.common.daynight

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Inject

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

@ApplicationScope
class DayNightHelper @Inject constructor(
    private val localConfigManager: LocalConfigManager,
) {

    companion object {
        private const val DAY_NIGHT_SETTING = "day_night_setting"
        private const val AMOLED_MODE = "amoled_mode"
    }

    private val _dayNightModeFlow: MutableStateFlow<DayNightMode>
    val dayNightModeFlow: StateFlow<DayNightMode>

    private val _amoledModeFlow: MutableStateFlow<Boolean>
    val amoledModeFlow: StateFlow<Boolean>

    init {
        val (modeValue, amoledEnabled) = runBlocking {
            getDayNightModeSetting() to getAmoledModeSetting()
        }
        setDefaultNightMode(modeValue)

        _dayNightModeFlow = MutableStateFlow(modeValue.toDayNightMode())
        dayNightModeFlow = _dayNightModeFlow.asStateFlow()

        _amoledModeFlow = MutableStateFlow(amoledEnabled)
        amoledModeFlow = _amoledModeFlow.asStateFlow()
    }

    // FIXME: use ActivityDayNightHelper.setMode before https://github.com/adrielcafe/voyager/issues/489 fix
    internal suspend fun setMode(mode: DayNightMode) {
        _dayNightModeFlow.value = mode
        localConfigManager.putInt(DAY_NIGHT_SETTING, mode.modeValue)
        setDefaultNightMode(mode.modeValue)
    }

    internal suspend fun setAmoledMode(enabled: Boolean) {
        _amoledModeFlow.value = enabled
        localConfigManager.putBoolean(AMOLED_MODE, enabled)
    }

    private suspend fun getDayNightModeSetting(): Int {
        return localConfigManager.getInt(DAY_NIGHT_SETTING)
            ?: DayNightMode.FOLLOW_SYSTEM.modeValue
    }

    private suspend fun getAmoledModeSetting(): Boolean {
        return localConfigManager.getBoolean(AMOLED_MODE) ?: false
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

internal expect fun setDefaultNightMode(modeValue: Int)

internal expect val DayNightMode.modeValue: Int

expect class ActivityDayNightHelper {
    val dayNightModeFlow: StateFlow<DayNightMode>
    val amoledModeFlow: StateFlow<Boolean>
    fun setMode(mode: DayNightMode)

    fun setAmoledMode(enabled: Boolean)
}

val LocalActivityDayNightHelper =
    staticCompositionLocalOf<ActivityDayNightHelper> { error("No ActivityDayNightHelper provided") }
