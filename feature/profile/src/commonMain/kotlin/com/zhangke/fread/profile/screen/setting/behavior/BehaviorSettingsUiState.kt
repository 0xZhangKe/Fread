package com.zhangke.fread.profile.screen.setting.behavior

import com.zhangke.fread.common.config.TimelineDefaultPosition

data class BehaviorSettingsUiState(
    val autoPlayInlineVideo: Boolean,
    val alwaysShowSensitiveContent: Boolean,
    val timelineDefaultPosition: TimelineDefaultPosition,
)
