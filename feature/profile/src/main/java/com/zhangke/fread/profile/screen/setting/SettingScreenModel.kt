package com.zhangke.fread.profile.screen.setting

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.language.LanguageHelper
import com.zhangke.fread.common.language.LanguageSettingType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class SettingScreenModel @Inject constructor(
    private val appContext: ApplicationContext,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingUiState(
            autoPlayInlineVideo = FreadConfigManager.autoPlayInlineVideo,
            dayNightMode = DayNightHelper.dayNightMode,
            languageSettingType = LanguageHelper.currentType,
            settingInfo = getAppVersionInfo(),
            contentSize = StatusContentSize.default(),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DayNightHelper.dayNightModeFlow.collect {
                _uiState.value = _uiState.value.copy(dayNightMode = DayNightHelper.dayNightMode)
            }
        }
        viewModelScope.launch {
            LanguageHelper.systemLocale
        }
        viewModelScope.launch {
            FreadConfigManager.getStatusContentSize(appContext)
                .let {
                    _uiState.value = _uiState.value.copy(contentSize = it)
                }
            FreadConfigManager.statusContentSizeFlow
                .collect {
                    _uiState.value = _uiState.value.copy(contentSize = it)
                }
        }
    }

    fun onChangeAutoPlayInlineVideo(on: Boolean) {
        viewModelScope.launch {
            FreadConfigManager.updateAutoPlayInlineVideo(on)
            _uiState.value = _uiState.value.copy(autoPlayInlineVideo = on)
        }
    }

    fun onChangeDayNightMode(mode: DayNightMode) {
        if (mode == DayNightHelper.dayNightMode) return
        DayNightHelper.setMode(mode)
    }

    fun onLanguageClick(context: Context, type: LanguageSettingType) {
        LanguageHelper.setLanguage(context, type)
    }

    fun onContentSizeChanged(contentSize: StatusContentSize) {
        viewModelScope.launch {
            FreadConfigManager.updateStatusContentSize(contentSize)
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
