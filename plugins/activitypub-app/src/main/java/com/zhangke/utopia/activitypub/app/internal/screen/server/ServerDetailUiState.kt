package com.zhangke.utopia.activitypub.app.internal.screen.server

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstance
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule
import com.zhangke.utopia.status.author.BlogAuthor

internal data class ServerDetailUiState(
    val loading: Boolean,
    val baseUrl: FormalBaseUrl?,
    val instance: ActivityPubInstance,
    val tabs: List<ServerDetailTab>,
)
