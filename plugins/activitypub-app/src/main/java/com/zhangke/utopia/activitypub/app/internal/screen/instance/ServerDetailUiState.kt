package com.zhangke.utopia.activitypub.app.internal.screen.instance

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstance

internal data class ServerDetailUiState(
    val loading: Boolean,
    val baseUrl: FormalBaseUrl?,
    val instance: ActivityPubInstance,
    val tabs: List<ServerDetailTab>,
)
