package com.zhangke.fread.profile.screen.setting.llm

data class LLmConfigUiState(
    val configs: List<LLmConfigItemUiState> = emptyList(),
)

data class LLmConfigItemUiState(
    val providerId: String,
    val modelName: String,
    val versionName: String,
    val apiKey: String,
    val selected: Boolean,
)
