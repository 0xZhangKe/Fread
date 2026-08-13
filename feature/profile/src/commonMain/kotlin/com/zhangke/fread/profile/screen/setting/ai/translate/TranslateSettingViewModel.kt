package com.zhangke.fread.profile.screen.setting.ai.translate

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
            val prompt = freadConfigManager.getTranslatePrompt()
            uiState.update { it.copy(targetLanguage = targetLanguage, prompt = prompt) }
        }
    }
}
