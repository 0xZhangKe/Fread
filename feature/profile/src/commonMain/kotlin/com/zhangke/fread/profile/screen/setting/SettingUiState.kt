package com.zhangke.fread.profile.screen.setting

import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.theme.ThemeType

data class SettingUiState(
    val autoPlayInlineVideo: Boolean,
    val alwaysShowSensitiveContent: Boolean,
    val immersiveNavBar: Boolean,
    val dayNightMode: DayNightMode,
    val settingInfo: String,
    val contentSize: StatusContentSize,
    val haveNewAppVersion: Boolean,
    val timelineDefaultPosition: TimelineDefaultPosition,
    val themeType: ThemeType,
)
