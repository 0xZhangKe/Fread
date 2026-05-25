package com.zhangke.fread.profile.screen.setting.llm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.common.ai.LLMModelConfigsRepo
import com.zhangke.fread.common.ai.model.LLMModelConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LLmConfigViewModel(
    private val modelConfigRepo: LLMModelConfigsRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LLmConfigUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            modelConfigRepo.getAllProviderFlow().collect { configs ->
                _uiState.update { current ->
                    current.copy(configs = configs.map { it.toUiState() })
                }
            }
        }
    }

    fun onSelectedChange(config: LLmConfigItemUiState) {
        if (config.selected) return
        viewModelScope.launch {
            modelConfigRepo.selectProvider(
                providerId = config.providerId,
                versionName = config.versionName,
            )
        }
    }

    fun onAddModelConfig(config: LLMModelConfig) {
        viewModelScope.launch {
            modelConfigRepo.insertProvider(config)
        }
    }

    fun onDeleteModelConfig(config: LLmConfigItemUiState) {
        viewModelScope.launch {
            modelConfigRepo.delete(config.providerId, config.versionName)
        }
    }

    private fun LLMModelConfig.toUiState(): LLmConfigItemUiState {
        return LLmConfigItemUiState(
            providerId = provider.id,
            modelName = provider.displayName,
            versionName = versionName,
            apiKey = apiKey,
            selected = selected,
        )
    }
}
