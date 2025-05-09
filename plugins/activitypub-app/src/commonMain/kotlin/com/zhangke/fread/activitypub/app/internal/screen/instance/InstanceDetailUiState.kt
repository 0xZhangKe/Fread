package com.zhangke.fread.activitypub.app.internal.screen.instance

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.author.BlogAuthor

data class InstanceDetailUiState(
    val loading: Boolean,
    val baseUrl: FormalBaseUrl?,
    val instance: ActivityPubInstanceEntity?,
    val modAccount: BlogAuthor?,
)
