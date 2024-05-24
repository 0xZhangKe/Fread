package com.zhangke.utopia.profile.screen.setting

import android.content.Context
import android.content.pm.PackageManager
import cafe.adriel.voyager.core.model.ScreenModel
import com.zhangke.framework.ktx.launchInScreenModel
import com.zhangke.utopia.common.daynight.DayNightHelper
import com.zhangke.utopia.common.daynight.DayNightMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SettingScreenModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        SettingUiState(
            dayNightMode = DayNightHelper.dayNightMode,
            settingInfo = getAppVersionInfo(),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        launchInScreenModel {
            DayNightHelper.dayNightModeFlow.collect {
                _uiState.value = _uiState.value.copy(dayNightMode = DayNightHelper.dayNightMode)
            }
        }
    }

    fun onChangeDayNightMode(mode: DayNightMode) {
        if (mode == DayNightHelper.dayNightMode) return
        DayNightHelper.setMode(mode)
    }

    private fun getAppVersionInfo(): String {
        return try {
            val info = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            @Suppress("DEPRECATION")
            "${info.versionName}(${info.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }
}