package com.zhangke.fread.profile.screen.setting

import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.language.LanguageSettingType

data class SettingUiState (
    val dayNightMode: DayNightMode,
    val languageSettingType: LanguageSettingType,
    val settingInfo: String,
)
