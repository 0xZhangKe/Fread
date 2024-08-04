package com.zhangke.fread.profile.screen.setting

import android.content.Context
import android.content.pm.PackageManager
import cafe.adriel.voyager.core.model.ScreenModel
import com.zhangke.framework.ktx.launchInScreenModel
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.AppFontSize
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.language.LanguageHelper
import com.zhangke.fread.common.language.LanguageSettingType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SettingScreenModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        SettingUiState(
            autoPlayInlineVideo = FreadConfigManager.autoPlayInlineVideo,
            dayNightMode = DayNightHelper.dayNightMode,
            languageSettingType = LanguageHelper.currentType,
            settingInfo = getAppVersionInfo(),
            contentSize = AppFontSize.MEDIUM,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        launchInScreenModel {
            DayNightHelper.dayNightModeFlow.collect {
                _uiState.value = _uiState.value.copy(dayNightMode = DayNightHelper.dayNightMode)
            }
        }
        launchInScreenModel {
            LanguageHelper.systemLocale
        }
        launchInScreenModel {
            FreadConfigManager.getAppFontSize(appContext)
                .let {
                    _uiState.value = _uiState.value.copy(contentSize = it)
                }
            FreadConfigManager.appFontSizeFlow
                .collect {
                    _uiState.value = _uiState.value.copy(contentSize = it)
                }
        }
    }

    fun onChangeAutoPlayInlineVideo(on: Boolean) {
        launchInScreenModel {
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

    fun onContentSizeChanged(contentSize: AppFontSize) {
        launchInScreenModel {
            FreadConfigManager.updateAppFontSize(contentSize)
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
