package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import com.zhangke.fread.status.model.PlatformLocator

data class HashtagTimelineUiState(
    val locator: PlatformLocator,
    val hashTag: String,
    val following: Boolean,
    val description: String,
)
