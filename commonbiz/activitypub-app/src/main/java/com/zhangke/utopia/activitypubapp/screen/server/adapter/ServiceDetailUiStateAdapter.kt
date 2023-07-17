package com.zhangke.utopia.activitypubapp.screen.server.adapter

import com.zhangke.activitypub.entry.ActivityPubInstanceEntity
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubUserAdapter
import com.zhangke.utopia.activitypubapp.model.ActivityPubInstanceRule
import com.zhangke.utopia.activitypubapp.screen.server.ServerDetailContract
import com.zhangke.utopia.activitypubapp.screen.server.ServerDetailTab
import com.zhangke.utopia.activitypubapp.screen.server.ServerDetailUiState
import javax.inject.Inject

internal class ServiceDetailUiStateAdapter @Inject constructor(
    private val userAdapter: ActivityPubUserAdapter,
) {

    fun createUiState(
        entity: ActivityPubInstanceEntity,
        loading: Boolean,
        tabs: List<ServerDetailTab>,
    ): ServerDetailUiState {
        return ServerDetailUiState(
            loading = loading,
            domain = entity.domain,
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
            account = userAdapter.createUser(entity.account)
        )
    }
}
