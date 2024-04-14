package com.zhangke.utopia.activitypub.app.internal.screen.hashtag

import com.zhangke.framework.network.FormalBaseUrl

data class HashtagTimelineUiState(
    val baseUrl: FormalBaseUrl,
    val hashTag: String,
    val following: Boolean,
    val description: String,
)
