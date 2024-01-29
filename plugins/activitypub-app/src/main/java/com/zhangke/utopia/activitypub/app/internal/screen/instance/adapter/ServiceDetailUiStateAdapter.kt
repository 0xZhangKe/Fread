package com.zhangke.utopia.activitypub.app.internal.screen.instance.adapter

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.screen.instance.ServerDetailTab
import com.zhangke.utopia.activitypub.app.internal.screen.instance.ServerDetailUiState
import javax.inject.Inject

internal class ServiceDetailUiStateAdapter @Inject constructor(
    private val activityPubInstanceAdapter: ActivityPubInstanceAdapter,
) {

    fun createUiState(
        entity: ActivityPubInstanceEntity,
        loading: Boolean,
        tabs: List<ServerDetailTab>,
    ): ServerDetailUiState {
        return ServerDetailUiState(
            loading = loading,
            baseUrl = FormalBaseUrl.parse(entity.domain)!!,
            instance = activityPubInstanceAdapter.toInstance(entity),
            tabs = tabs,
        )
    }
}
