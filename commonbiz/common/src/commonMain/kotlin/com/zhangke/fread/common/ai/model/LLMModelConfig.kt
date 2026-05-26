package com.zhangke.fread.common.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class LLMModelConfig(
    val provider: LLMProvider,
    val versionName: String,
    val apiKey: String,
    val selected: Boolean = false,
)
