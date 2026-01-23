package com.zhangke.fread.profile.screen.setting.behavior

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.TimelineDefaultPosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BehaviorSettingsViewModel(
    private val freadConfigManager: FreadConfigManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BehaviorSettingsUiState(
            autoPlayInlineVideo = freadConfigManager.autoPlayInlineVideo,
            alwaysShowSensitiveContent = false,
            timelineDefaultPosition = TimelineDefaultPosition.NEWEST,
            openUrlInAppBrowser = freadConfigManager.openUrlInAppBrowser,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            freadConfigManager.getTimelineDefaultPosition()
                .let { position ->
                    _uiState.update { it.copy(timelineDefaultPosition = position) }
                }
        }
        viewModelScope.launch {
            freadConfigManager.statusConfigFlow.collect { config ->
                _uiState.update {
                    it.copy(alwaysShowSensitiveContent = config.alwaysShowSensitiveContent)
                }
            }
        }
    }

    fun onChangeAutoPlayInlineVideo(on: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateAutoPlayInlineVideo(on)
            _uiState.update { it.copy(autoPlayInlineVideo = on) }
        }
    }

    fun onAlwaysShowSensitiveContentChanged(always: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateAlwaysShowSensitiveContent(always)
        }
    }

    fun onTimelineDefaultPositionChanged(position: TimelineDefaultPosition) {
        viewModelScope.launch {
            freadConfigManager.updateTimelineDefaultPosition(position)
            _uiState.update { it.copy(timelineDefaultPosition = position) }
        }
    }

    fun onOpenUrlInAppBrowserChanged(openInApp: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateOpenUrlInAppBrowser(openInApp)
            _uiState.update { it.copy(openUrlInAppBrowser = openInApp) }
        }
    }
}
