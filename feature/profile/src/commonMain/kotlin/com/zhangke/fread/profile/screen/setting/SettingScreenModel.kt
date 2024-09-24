package com.zhangke.fread.profile.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.handler.TextHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class SettingScreenModel @Inject constructor(
    private val textHandler: TextHandler,
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
            freadConfigManager.statusConfigFlow
                .collect {
                    _uiState.value = _uiState.value.copy(contentSize = it.contentSize)
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

    private fun getAppVersionInfo(): String {
        return "${textHandler.versionName}(${textHandler.versionCode})"
    }
}
