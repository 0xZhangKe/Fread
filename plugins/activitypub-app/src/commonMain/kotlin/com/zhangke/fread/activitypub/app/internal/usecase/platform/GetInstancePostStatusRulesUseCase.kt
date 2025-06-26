package com.zhangke.fread.activitypub.app.internal.usecase.platform

import com.zhangke.activitypub.entities.ActivityPubInstanceConfigurationEntity
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostBlogRules
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject

class GetInstancePostStatusRulesUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(locator: PlatformLocator): Result<PostBlogRules> {
        return clientManager.getClient(locator)
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
