package com.zhangke.fread.activitypub.app.internal.usecase.platform

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostBlogRules
import com.zhangke.fread.status.model.PlatformLocator

class GetInstancePostStatusRulesUseCase (
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(locator: PlatformLocator): Result<PostBlogRules> {
        return clientManager.getClient(locator)
            .instanceRepo
            .getInstanceInformation()
            .map { it.toRule() }
    }

    private fun ActivityPubInstanceEntity.toRule(): PostBlogRules {
        val config = configuration
        return PostBlogRules.default(
            maxCharacters = config.statuses?.maxCharacters ?: 0,
            maxMediaCount = config.statuses?.maxMediaAttachments ?: 0,
            maxPollOptions = config.polls?.maxOptions ?: 0,
            supportsQuotePost = this.supportsQuotePost,
        )
    }
}