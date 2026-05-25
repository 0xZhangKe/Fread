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
                apiKey = freadConfigManager.getAltTextApiKey(),
                baseUrl = freadConfigManager.getAltTextBaseUrl(),
                model = freadConfigManager.getAltTextModel(),
                prompt = freadConfigManager.getAltTextPrompt(),
                maxTokens = freadConfigManager.getAltTextMaxTokens().toString(),
            )
        }
    }

    fun onApiKeyChange(value: String) {
        _uiState.update { it.copy(apiKey = value) }
        viewModelScope.launch { freadConfigManager.updateAltTextApiKey(value) }
    }

    fun onBaseUrlChange(value: String) {
        _uiState.update { it.copy(baseUrl = value) }
        viewModelScope.launch { freadConfigManager.updateAltTextBaseUrl(value) }
    }

    fun onModelChange(value: String) {
        _uiState.update { it.copy(model = value) }
        viewModelScope.launch { freadConfigManager.updateAltTextModel(value) }
    }

    fun onPromptChange(value: String) {
        _uiState.update { it.copy(prompt = value) }
        viewModelScope.launch { freadConfigManager.updateAltTextPrompt(value) }
    }

    fun onMaxTokensChange(value: String) {
        // Allow free editing of the field; only commit a valid positive int.
        _uiState.update { it.copy(maxTokens = value) }
        val parsed = value.toIntOrNull()
        if (parsed != null && parsed > 0) {
            viewModelScope.launch { freadConfigManager.updateAltTextMaxTokens(parsed) }
        }
    }
}
