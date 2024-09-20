package com.zhangke.fread.profile.screen.setting

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.di.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class SettingScreenModel @Inject constructor(
    private val appContext: ApplicationContext,
    private val freadConfigManager: FreadConfigManager,
    private val dayNightHelper: DayNightHelper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingUiState(
            autoPlayInlineVideo = freadConfigManager.autoPlayInlineVideo,
            dayNightMode = dayNightHelper.dayNightModeFlow.value,
            settingInfo = getAppVersionInfo(),
            contentSize = StatusContentSize.default(),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dayNightHelper.dayNightModeFlow.collect {
                _uiState.value = _uiState.value.copy(dayNightMode = it)
            }
        }
        viewModelScope.launch {
            freadConfigManager.getStatusContentSize()
                .let {
                    _uiState.value = _uiState.value.copy(contentSize = it)
                }
            freadConfigManager.statusContentSizeFlow
                .collect {
                    _uiState.value = _uiState.value.copy(contentSize = it)
                }
        }
    }

    fun onChangeAutoPlayInlineVideo(on: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateAutoPlayInlineVideo(on)
            _uiState.value = _uiState.value.copy(autoPlayInlineVideo = on)
        }
    }

    fun onChangeDayNightMode(mode: DayNightMode) {
        if (mode == dayNightHelper.dayNightModeFlow.value) return
        viewModelScope.launch {
            dayNightHelper.setMode(mode)
        }
    }

    fun onContentSizeChanged(contentSize: StatusContentSize) {
        viewModelScope.launch {
            freadConfigManager.updateStatusContentSize(contentSize)
        }
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
