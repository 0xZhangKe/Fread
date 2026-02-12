package com.zhangke.fread.profile.screen.setting.appearance

import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.theme.ThemeType

data class AppearanceSettingsUiState(
    val immersiveNavBar: Boolean,
    val blurAppBarStyleEnabled: Boolean,
    val amoledEnabled: Boolean,
    val dayNightMode: DayNightMode,
    val contentSize: StatusContentSize,
    val themeType: ThemeType,
    val homeTabNextButtonVisible: Boolean,
    val homeTabRefreshButtonVisible: Boolean,
)
