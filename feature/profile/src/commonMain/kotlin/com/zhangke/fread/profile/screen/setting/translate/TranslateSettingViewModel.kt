package com.zhangke.fread.profile.screen.setting.translate

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.config.FreadConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TranslateSettingViewModel(
    private val freadConfigManager: FreadConfigManager,
) : ViewModel() {

    val uiState: StateFlow<TranslateSettingUiState>
        field = MutableStateFlow(TranslateSettingUiState())

    init {
        loadConfig()
    }

    private fun loadConfig() {
        launchInViewModel {
            val targetLanguage = freadConfigManager.getTranslateTargetLanguage()
            val enabled = freadConfigManager.getAiTranslateEnabled()
            uiState.update { it.copy(targetLanguage = targetLanguage, enabled = enabled) }
        }
    }

    fun onLanguageSelected(language: String) {
        launchInViewModel {
            freadConfigManager.updateTranslateTargetLanguage(language)
            uiState.update { it.copy(targetLanguage = language) }
        }
    }

    fun onAiTranslateEnableChanged(enabled: Boolean) {
        launchInViewModel {
            freadConfigManager.updateAiTranslateEnabled(enabled)
            uiState.update { it.copy(enabled = enabled) }
        }
    }
}
