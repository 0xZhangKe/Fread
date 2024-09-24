package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import com.zhangke.fread.status.model.IdentityRole

data class HashtagTimelineUiState(
    val role: IdentityRole,
    val hashTag: String,
    val following: Boolean,
    val description: String,
)
