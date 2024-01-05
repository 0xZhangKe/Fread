package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.network.FormalBaseUrl

data class ActivityPubInstance (
    val baseUrl: FormalBaseUrl?,
    val title: String,
    val description: String,
    val thumbnail: String,
    val version: String,
    val activeMonth: Int,
    val languages: List<String>,
    val contract: ServerDetailContract? = null,
    val rules: List<ActivityPubInstanceRule>,
)