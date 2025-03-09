package com.zhangke.fread.activitypub.app.internal.usecase.platform

import com.zhangke.activitypub.entities.ActivityPubInstanceConfigurationEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostBlogRules
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class GetInstancePostStatusRulesUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(baseUrl: FormalBaseUrl): Result<PostBlogRules> {
        return clientManager.getClient(IdentityRole(baseUrl = baseUrl, accountUri = null))
            .instanceRepo
            .getInstanceInformation()
            .map { it.configuration.toRule() }
    }

    private fun ActivityPubInstanceConfigurationEntity.toRule(): PostBlogRules {
        return PostBlogRules.default(
            maxCharacters = this.statuses?.maxCharacters ?: 0,
            maxMediaCount = this.statuses?.maxMediaAttachments ?: 0,
            maxPollOptions = this.polls?.maxOptions ?: 0,
        )
    }
}
