package com.zhangke.fread.activitypub.app

import com.zhangke.activitypub.entities.ActivityPubInstanceConfigurationEntity
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.utils.initLocale
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusAttachment
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusMediaAttachmentFile
import com.zhangke.fread.activitypub.app.internal.screen.status.post.usecase.PublishPostUseCase
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.publish.IPublishBlogManager
import com.zhangke.fread.status.publish.PublishingMedia
import com.zhangke.fread.status.publish.PublishingPost
import me.tatarka.inject.annotations.Inject

class ActivityPubPublishManager @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val publishPost: PublishPostUseCase,
) : IPublishBlogManager {

    private val baseUrlToInstanceInfo = mutableMapOf<String, ActivityPubInstanceEntity>()

    override suspend fun getPublishBlogRules(account: LoggedAccount): Result<PublishBlogRules>? {
        if (account.platform.protocol.notActivityPub) return null
        baseUrlToInstanceInfo[account.platform.baseUrl.toString()]?.let { instance ->
            return Result.success(instance.configuration.toRules())
        }
        val locator = PlatformLocator(accountUri = account.uri, baseUrl = account.platform.baseUrl)
        return clientManager.getClient(locator)
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
            mediaAltMaxCharacters = 1500,
        )
    }

    override suspend fun publish(
        account: LoggedAccount,
        post: PublishingPost
    ): Result<Unit>? {
        if (account.platform.protocol.notActivityPub) return null
        val apAccount = account as ActivityPubLoggedAccount
        return publishPost(
            account = apAccount,
            content = post.content,
            attachment = post.medias.convert(),
            sensitive = post.sensitive,
            warningContent = post.warningText,
            visibility = post.visibility,
            language = initLocale(post.languageCode),
        )
    }

    private fun List<PublishingMedia>.convert(): PostStatusAttachment? {
        if (this.isEmpty()) return null
        if (this.first().isVideo) {
            return PostStatusAttachment.Video(this.first().convert())
        }
        return PostStatusAttachment.Image(this.map { it.convert() })
    }

    private fun PublishingMedia.convert(): PostStatusMediaAttachmentFile {
        return PostStatusMediaAttachmentFile.LocalFile(
            file = this.file,
            alt = this.alt,
        )
    }
}
