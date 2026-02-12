package com.zhangke.fread.profile.screen.setting.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.theme.ThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppearanceSettingsViewModel(
    private val freadConfigManager: FreadConfigManager,
    private val dayNightHelper: DayNightHelper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AppearanceSettingsUiState(
            immersiveNavBar = freadConfigManager.statusConfigFlow.value.immersiveNavBar,
            blurAppBarStyleEnabled = freadConfigManager.enableBlurAppBarStyleFlow.value,
            amoledEnabled = dayNightHelper.amoledModeFlow.value,
            dayNightMode = dayNightHelper.dayNightModeFlow.value,
            contentSize = freadConfigManager.statusConfigFlow.value.contentSize,
            themeType = ThemeType.DEFAULT,
            homeTabNextButtonVisible = freadConfigManager.homeTabNextButtonVisibleFlow.value,
            homeTabRefreshButtonVisible = freadConfigManager.homeTabRefreshButtonVisibleFlow.value,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            freadConfigManager.getThemeType()
                .let { type ->
                    _uiState.update { it.copy(themeType = type) }
                }
        }
        viewModelScope.launch {
            dayNightHelper.dayNightModeFlow.collect { dayNightMode ->
                _uiState.update { it.copy(dayNightMode = dayNightMode) }
            }
        }
        viewModelScope.launch {
            dayNightHelper.amoledModeFlow.collect { enabled ->
                _uiState.update { it.copy(amoledEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            freadConfigManager.statusConfigFlow.collect { config ->
                _uiState.update {
                    it.copy(
                        contentSize = config.contentSize,
                        immersiveNavBar = config.immersiveNavBar,
                    )
                }
            }
        }
        viewModelScope.launch {
            freadConfigManager.homeTabRefreshButtonVisibleFlow.collect { visible ->
                _uiState.update { it.copy(homeTabRefreshButtonVisible = visible) }
            }
        }
        viewModelScope.launch {
            freadConfigManager.homeTabNextButtonVisibleFlow.collect { visible ->
                _uiState.update { it.copy(homeTabNextButtonVisible = visible) }
            }
        }
        viewModelScope.launch {
            freadConfigManager.enableBlurAppBarStyleFlow.collect { enabled ->
                _uiState.update { it.copy(blurAppBarStyleEnabled = enabled) }
            }
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

    fun onImmersiveBarChanged(on: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateImmersiveNavBar(on)
        }
    }

    fun onHomeTabNextButtonVisibleChanged(visible: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateHomeTabNextButtonVisible(visible)
        }
    }

    fun onHomeTabRefreshButtonVisibleChanged(visible: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateHomeTabRefreshButtonVisible(visible)
        }
    }

    fun onBlurAppBarStyleChanged(enabled: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateEnableBlurAppBarStyle(enabled)
        }
    }
}
