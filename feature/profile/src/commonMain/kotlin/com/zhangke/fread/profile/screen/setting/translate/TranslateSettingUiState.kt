package com.zhangke.fread.profile.screen.setting.translate

data class TranslateSettingUiState(
    val enabled: Boolean = false,
    val targetLanguage: String? = null,
    val prompt: String = "",
)
