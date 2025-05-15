package com.zhangke.fread.activitypub.app

import com.zhangke.activitypub.entities.ActivityPubInstanceConfigurationEntity
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.publish.IPublishBlogManager
import me.tatarka.inject.annotations.Inject

class ActivityPubPublishManager @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) : IPublishBlogManager {

    private val baseUrlToInstanceInfo = mutableMapOf<String, ActivityPubInstanceEntity>()

    override suspend fun getPublishBlogRules(account: LoggedAccount): Result<PublishBlogRules>? {
        if (account.platform.protocol.notActivityPub) return null
        baseUrlToInstanceInfo[account.platform.baseUrl.toString()]?.let { instance ->
            return Result.success(instance.configuration.toRules())
        }
        return clientManager.getClient(IdentityRole(account.uri, account.platform.baseUrl))
            .instanceRepo
            .getInstanceInformation()
            .map {
                baseUrlToInstanceInfo[account.platform.baseUrl.toString()] = it
                it.configuration.toRules()
            }
    }

    private fun ActivityPubInstanceConfigurationEntity.toRules(): PublishBlogRules {
        return PublishBlogRules(
            maxCharacters = this.statuses?.maxCharacters ?: 200,
            maxMediaCount = this.statuses?.maxMediaAttachments ?: 4,
            maxPollOptions = this.polls?.maxOptions ?: 4,
            supportSpoiler = true,
            supportPoll = true,
            maxLanguageCount = 1,
        )
    }
}
