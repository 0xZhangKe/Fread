package com.zhangke.utopia.activitypub.app.internal.screen.server.adapter

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule
import com.zhangke.utopia.activitypub.app.internal.screen.server.ServerDetailContract
import com.zhangke.utopia.activitypub.app.internal.screen.server.ServerDetailTab
import com.zhangke.utopia.activitypub.app.internal.screen.server.ServerDetailUiState
import javax.inject.Inject

internal class ServiceDetailUiStateAdapter @Inject constructor(
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
) {

    fun createUiState(
        entity: ActivityPubInstanceEntity,
        loading: Boolean,
        tabs: List<ServerDetailTab>,
    ): ServerDetailUiState {
        return ServerDetailUiState(
            loading = loading,
            baseUrl = FormalBaseUrl.parse(entity.domain)!!,
            title = entity.title,
            description = entity.description,
            thumbnail = entity.thumbnail.url,
            version = entity.version,
            activeMonth = entity.usage.users.activeMonth,
            languages = entity.languages,
            rules = entity.rules.map(::convertRule),
            contract = convertContract(entity.contact),
            tabs = tabs,
        )
    }

    private fun convertRule(entity: ActivityPubInstanceEntity.Rule): ActivityPubInstanceRule {
        return ActivityPubInstanceRule(id = entity.id, text = entity.text)
    }

    private fun convertContract(entity: ActivityPubInstanceEntity.Contact): ServerDetailContract {
        return ServerDetailContract(
            email = entity.email,
            account = accountEntityAdapter.toAuthor(entity.account),
        )
    }
}
