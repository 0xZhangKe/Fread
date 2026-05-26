package com.zhangke.fread.profile.screen.setting.alttext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.common.config.FreadConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AltTextSettingsViewModel(
    private val freadConfigManager: FreadConfigManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AltTextSettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = AltTextSettingsUiState(
                prompt = freadConfigManager.getAltTextPrompt(),
            )
        }
    }

    fun onPromptChange(value: String) {
        _uiState.update { it.copy(prompt = value) }
        viewModelScope.launch { freadConfigManager.updateAltTextPrompt(value) }
    }
}
