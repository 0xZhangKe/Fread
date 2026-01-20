package com.zhangke.fread.profile.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.handler.TextHandler
import com.zhangke.fread.common.theme.ThemeType
import com.zhangke.fread.common.update.AppUpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingScreenModel(
    private val textHandler: TextHandler,
    private val freadConfigManager: FreadConfigManager,
    private val dayNightHelper: DayNightHelper,
    private val updateManager: AppUpdateManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingUiState(
            autoPlayInlineVideo = freadConfigManager.autoPlayInlineVideo,
            dayNightMode = dayNightHelper.dayNightModeFlow.value,
            immersiveNavBar = false,
            amoledEnabled = dayNightHelper.amoledModeFlow.value,
            settingInfo = getAppVersionInfo(),
            contentSize = StatusContentSize.default(),
            alwaysShowSensitiveContent = false,
            haveNewAppVersion = false,
            timelineDefaultPosition = TimelineDefaultPosition.NEWEST,
            themeType = ThemeType.DEFAULT,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel {
            freadConfigManager.getTimelineDefaultPosition()
                .let { position ->
                    _uiState.update { it.copy(timelineDefaultPosition = position) }
                }
            freadConfigManager.getThemeType()
                .let { type ->
                    _uiState.update { it.copy(themeType = type) }
                }
        }
        viewModelScope.launch {
            dayNightHelper.dayNightModeFlow
                .collect { dayNightMode ->
                    _uiState.update { it.copy(dayNightMode = dayNightMode) }
                }
        }
        viewModelScope.launch {
            dayNightHelper.amoledModeFlow.collect { enabled ->
                _uiState.update { it.copy(amoledEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            freadConfigManager.statusConfigFlow
                .collect { config ->
                    _uiState.update {
                        it.copy(
                            contentSize = config.contentSize,
                            alwaysShowSensitiveContent = config.alwaysShowSensitiveContent,
                            immersiveNavBar = config.immersiveNavBar,
                        )
                    }
                }
        }
        viewModelScope.launch {
            if (updateManager.enableAutoCheckUpdate) {
                updateManager.checkForUpdate(false)
                    .onSuccess { (needUpdate, _) ->
                        _uiState.update { it.copy(haveNewAppVersion = needUpdate) }
                    }
            }
        }
    }

    fun onChangeAutoPlayInlineVideo(on: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateAutoPlayInlineVideo(on)
            _uiState.value = _uiState.value.copy(autoPlayInlineVideo = on)
        }
    }

    fun onThemeTypeChanged(type: ThemeType) {
        viewModelScope.launch {
            freadConfigManager.updateThemeType(type)
            _uiState.update { it.copy(themeType = type) }
        }
    }

    fun onContentSizeChanged(contentSize: StatusContentSize) {
        viewModelScope.launch {
            freadConfigManager.updateStatusContentSize(contentSize)
        }
    }

    fun onAlwaysShowSensitiveContentChanged(always: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateAlwaysShowSensitiveContent(always)
        }
    }

    fun onImmersiveBarChanged(on: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateImmersiveNavBar(on)
        }
    }

    fun onTimelineDefaultPositionChanged(position: TimelineDefaultPosition) {
        viewModelScope.launch {
            freadConfigManager.updateTimelineDefaultPosition(position)
            _uiState.update { it.copy(timelineDefaultPosition = position) }
        }
    }

    private fun getAppVersionInfo(): String {
        return "${textHandler.versionName}(${textHandler.versionCode})"
    }
}
