package com.zhangke.fread.profile.screen.setting

import com.zhangke.fread.common.ai.model.LLMModelConfig

data class SettingUiState(
    val settingInfo: String,
    val haveNewAppVersion: Boolean,
    val currentLLMModel: LLMModelConfig? = null,
)
