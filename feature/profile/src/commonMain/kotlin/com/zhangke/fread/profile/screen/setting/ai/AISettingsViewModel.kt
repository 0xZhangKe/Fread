package com.zhangke.fread.profile.screen.setting.ai

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.ai.LLMModelConfigsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AISettingsViewModel(
    private val modelConfigRepo: LLMModelConfigsRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AISettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel {
            modelConfigRepo.getAllProviderFlow().collect { models ->
                _uiState.update {
                    it.copy(currentLLMModel = models.firstOrNull { model -> model.selected })
                }
            }
        }
    }
}
