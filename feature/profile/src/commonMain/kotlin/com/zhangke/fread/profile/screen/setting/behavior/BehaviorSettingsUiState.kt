package com.zhangke.fread.profile.screen.setting.behavior

import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.status.preference.FollowingFeedPrefs

data class BehaviorSettingsUiState(
    val autoPlayInlineVideo: Boolean,
    val alwaysShowSensitiveContent: Boolean,
    val timelineDefaultPosition: TimelineDefaultPosition,
    val openUrlInAppBrowser: Boolean,
    val jumpToProfile: Boolean,
    val showFollowingFeedSection: Boolean = false,
    val followingFeedPrefs: FollowingFeedPrefs = FollowingFeedPrefs(),
)
