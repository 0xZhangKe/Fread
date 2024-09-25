package com.zhangke.fread.profile.screen.setting

import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightMode

data class SettingUiState(
    val autoPlayInlineVideo: Boolean,
    val alwaysShowSensitiveContent: Boolean,
    val dayNightMode: DayNightMode,
    val settingInfo: String,
    val contentSize: StatusContentSize,
)
