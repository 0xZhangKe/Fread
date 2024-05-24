package com.zhangke.utopia.profile.screen.setting

import com.zhangke.utopia.common.daynight.DayNightMode
import com.zhangke.utopia.common.language.LanguageSettingType

data class SettingUiState (
    val dayNightMode: DayNightMode,
    val languageSettingType: LanguageSettingType,
    val settingInfo: String,
)
