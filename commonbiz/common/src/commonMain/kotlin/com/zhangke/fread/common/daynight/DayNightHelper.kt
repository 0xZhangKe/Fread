package com.zhangke.fread.common.daynight

import androidx.compose.runtime.staticCompositionLocalOf
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
        private const val AMOLED_MODE = "amoled_mode"
    }

    private val _dayNightModeFlow: MutableStateFlow<DayNightMode>
    val dayNightModeFlow: StateFlow<DayNightMode>

    private val _amoledModeFlow: MutableStateFlow<Boolean>
    val amoledModeFlow: StateFlow<Boolean>

    private val dayNightPlatformHelper = DayNightPlatformHelper()

    init {
        val (model, amoledEnabled) = runBlocking {
            getDayNightModeSetting() to getAmoledModeSetting()
        }
        dayNightPlatformHelper.setDefaultMode(model)

        _dayNightModeFlow = MutableStateFlow(model)
        dayNightModeFlow = _dayNightModeFlow.asStateFlow()

        _amoledModeFlow = MutableStateFlow(amoledEnabled)
        amoledModeFlow = _amoledModeFlow.asStateFlow()
    }

    fun setDefaultMode() {
        dayNightPlatformHelper.setDefaultMode(dayNightModeFlow.value)
    }

    // FIXME: use ActivityDayNightHelper.setMode before https://github.com/adrielcafe/voyager/issues/489 fix
    suspend fun setMode(mode: DayNightMode) {
        _dayNightModeFlow.value = mode
        localConfigManager.putInt(DAY_NIGHT_SETTING, mode.localKey)
        dayNightPlatformHelper.setMode(mode)
    }

    suspend fun setAmoledMode(enabled: Boolean) {
        _amoledModeFlow.value = enabled
        localConfigManager.putBoolean(AMOLED_MODE, enabled)
        dayNightPlatformHelper.setAmoledMode(enabled)
    }

    private suspend fun getDayNightModeSetting(): DayNightMode {
        return localConfigManager.getInt(DAY_NIGHT_SETTING)
            ?.let { DayNightMode.fromLocalKey(it) } ?: DayNightMode.FOLLOW_SYSTEM
    }

    private suspend fun getAmoledModeSetting(): Boolean {
        return localConfigManager.getBoolean(AMOLED_MODE) ?: false
    }

}

expect class DayNightPlatformHelper() {

    fun setDefaultMode(modeValue: DayNightMode)

    fun setMode(mode: DayNightMode)

    fun setAmoledMode(enabled: Boolean)
}

val LocalActivityDayNightHelper =
    staticCompositionLocalOf<DayNightHelper> { error("No ActivityDayNightHelper provided") }
