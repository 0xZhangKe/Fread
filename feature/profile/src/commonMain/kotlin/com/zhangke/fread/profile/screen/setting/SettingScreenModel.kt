package com.zhangke.fread.profile.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.handler.TextHandler
import com.zhangke.fread.common.update.AppUpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class SettingScreenModel @Inject constructor(
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
            settingInfo = getAppVersionInfo(),
            contentSize = StatusContentSize.default(),
            alwaysShowSensitiveContent = false,
            haveNewAppVersion = false,
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

    private fun getAppVersionInfo(): String {
        return "${textHandler.versionName}(${textHandler.versionCode})"
    }
}
