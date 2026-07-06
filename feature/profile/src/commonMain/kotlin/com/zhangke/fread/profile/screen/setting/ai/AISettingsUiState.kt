package com.zhangke.fread.profile.screen.setting.ai

import com.zhangke.fread.common.ai.model.LLMModelConfig

data class AISettingsUiState(
    val currentLLMModel: LLMModelConfig? = null,
)
