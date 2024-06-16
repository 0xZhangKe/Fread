package com.zhangke.fread.activitypub.app.internal.adapter

import android.content.Context
import com.zhangke.activitypub.entities.ActivityPubMediaAttachmentEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ActivityPubStatusAdapter @Inject constructor(
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val getStatusSupportInteraction: GetStatusInteractionUseCase,
    private val activityPubAccountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val metaAdapter: ActivityPubBlogMetaAdapter,
    private val pollAdapter: ActivityPubPollAdapter,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    @ApplicationContext private val context: Context,
) {

    suspend fun toStatus(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
    ): Status {
        val supportActions = getStatusSupportInteraction(entity, platform)
        return if (entity.reblog != null) {
            entity.toReblog(supportActions, platform)
        } else {
            entity.toNewBlog(supportActions, platform)
        }
    }

    private fun ActivityPubStatusEntity.toNewBlog(
        supportActions: List<StatusInteraction>,
        platform: BlogPlatform,
    ): Status.NewBlog {
        return Status.NewBlog(toBlog(platform), supportActions)
    }

    private fun ActivityPubStatusEntity.toReblog(
        supportActions: List<StatusInteraction>,
        platform: BlogPlatform,
    ): Status.Reblog {
        return Status.Reblog(
            author = activityPubAccountEntityAdapter.toAuthor(account),
            id = id,
            datetime = formatDatetimeToDate(createdAt).time,
            reblog = reblog!!.toBlog(platform),
            supportInteraction = supportActions,
        )
    }

    fun entityToBlog(entity: ActivityPubStatusEntity, platform: BlogPlatform): Blog {
        return entity.toBlog(platform)
    }

    private fun ActivityPubStatusEntity.toBlog(platform: BlogPlatform): Blog {
        val emojis = this.emojis.map(emojiEntityAdapter::toEmoji)
        return Blog(
            id = id,
            author = activityPubAccountEntityAdapter.toAuthor(account),
            title = null,
            description = null,
            content = content,
            sensitive = sensitive,
            spoilerText = spoilerText,
            date = formatDatetimeToDate(createdAt),
            url = this.url.ifNullOrEmpty { this.uri },
            forwardCount = reblogsCount,
            likeCount = favouritesCount,
            repliesCount = repliesCount,
            platform = platform,
            mediaList = mediaAttachments?.map { it.toBlogMedia() } ?: emptyList(),
            poll = poll?.let(pollAdapter::adapt),
            emojis = emojis,
            mentions = this.mentions.mapNotNull { it.toMention() },
            tags = tags.map { it.toTag() },
        )
    }

    private fun ActivityPubMediaAttachmentEntity.toBlogMedia(): BlogMedia {
        val mediaType = convertMediaType(type)
        return BlogMedia(
            id = id,
            url = url,
            type = mediaType,
            previewUrl = previewUrl,
            remoteUrl = remoteUrl,
            description = description,
            meta = this.meta?.let { metaAdapter.adapt(mediaType, it) },
            blurhash = blurhash,
        )
    }

    private fun ActivityPubStatusEntity.Tag.toTag(): HashtagInStatus {
        return HashtagInStatus(
            name = name,
            url = url,
            protocol = createActivityPubProtocol(context),
        )
    }

    private fun convertMediaType(type: String): BlogMediaType {
        return when (type) {
            ActivityPubMediaAttachmentEntity.TYPE_IMAGE -> BlogMediaType.IMAGE
            ActivityPubMediaAttachmentEntity.TYPE_AUDIO -> BlogMediaType.AUDIO
            ActivityPubMediaAttachmentEntity.TYPE_VIDEO -> BlogMediaType.VIDEO
            ActivityPubMediaAttachmentEntity.TYPE_GIFV -> BlogMediaType.GIFV
            ActivityPubMediaAttachmentEntity.TYPE_UNKNOWN -> BlogMediaType.UNKNOWN
            else -> throw IllegalArgumentException("Unsupported media type(${type})!")
        }
    }

    private fun ActivityPubStatusEntity.Mention.toMention(): Mention? {
        val webFinger = WebFinger.create(acct) ?: WebFinger.create(this.url) ?: return null
        return Mention(
            id = id,
            username = username,
            url = url,
            webFinger = webFinger,
            protocol = createActivityPubProtocol(context),
        )
    }
}
