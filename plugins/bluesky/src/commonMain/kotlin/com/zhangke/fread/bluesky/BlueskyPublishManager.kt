package com.zhangke.fread.bluesky

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostMediaAttachment
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostMediaAttachmentFile
import com.zhangke.fread.bluesky.internal.usecase.PublishingPostUseCase
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.publish.IPublishBlogManager
import com.zhangke.fread.status.publish.PublishingMedia
import com.zhangke.fread.status.publish.PublishingPost

class BlueskyPublishManager (
    private val publishingPost: PublishingPostUseCase,
) : IPublishBlogManager {

    override suspend fun getPublishBlogRules(account: LoggedAccount): Result<PublishBlogRules>? {
        if (account.platform.protocol.notBluesky) return null
        return Result.success(
            PublishBlogRules(
                maxCharacters = 300,
                maxMediaCount = 4,
                maxPollOptions = 0,
                supportSpoiler = false,
                supportPoll = false,
                maxLanguageCount = 2,
                mediaAltMaxCharacters = 2000,
            )
        )
    }

    override suspend fun publish(
        account: LoggedAccount,
        post: PublishingPost
    ): Result<Unit>? {
        if (account.platform.protocol.notBluesky) return null
        val bskyAccount = account as BlueskyLoggedAccount
        return publishingPost(
            account = bskyAccount,
            content = post.content,
            interactionSetting = post.interactionSetting,
            selectedLanguages = listOf(post.languageCode),
            attachment = post.medias.convert(),
        )
    }

    private fun List<PublishingMedia>.convert(): PublishPostMediaAttachment? {
        if (this.isEmpty()) return null
        if (this.first().isVideo) {
            return PublishPostMediaAttachment.Video(this.first().convert())
        }
        return PublishPostMediaAttachment.Image(this.map { it.convert() })
    }

    private fun PublishingMedia.convert(): PublishPostMediaAttachmentFile {
        return PublishPostMediaAttachmentFile(
            file = this.file,
            alt = this.alt,
            isVideo = this.isVideo,
        )
    }
}