package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubMediaAttachmentEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.emoji.MapCustomEmojiUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaType
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusInteraction
import javax.inject.Inject

class ActivityPubStatusAdapter @Inject constructor(
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val getStatusSupportInteraction: GetStatusInteractionUseCase,
    private val activityPubAccountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val metaAdapter: ActivityPubBlogMetaAdapter,
    private val pollAdapter: ActivityPubPollAdapter,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    private val mapCustomEmoji: MapCustomEmojiUseCase,
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

    fun toEntity(status: Status): ActivityPubStatusEntity{
        return ActivityPubStatusEntity(
            id = status.id,
            account = activityPubAccountEntityAdapter.toEntity(status.author),
            content = status.content,
            sensitive = status.sensitive,
            spoilerText = status.spoilerText,
            createdAt = formatDatetimeToDate.formatDateToDatetime(status.datetime),
            reblogsCount = status.forwardCount,
            favouritesCount = status.likeCount,
            repliesCount = status.repliesCount,
            emojis = status.emojis.map(emojiEntityAdapter::toEntity),
            mentions = status.mentions.mapNotNull { mention ->
                ActivityPubStatusEntity.Mention(
                    id = mention.id,
                    username = mention.username,
                    url = mention.url,
                    acct = mention.webFinger.toString(),
                )
            },
            mediaAttachments = status.mediaList.map { media ->
                ActivityPubMediaAttachmentEntity(
                    id = media.id,
                    type = media.type.name,
                    url = media.url,
                    previewUrl = media.previewUrl,
                    remoteUrl = media.remoteUrl,
                    description = media.description,
                    meta = media.meta?.let { metaAdapter.adapt(it) },
                    blurhash = media.blurhash,
                )
            },
            poll = status.poll?.let(pollAdapter::adapt),
        )
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

    private fun ActivityPubStatusEntity.toBlog(platform: BlogPlatform): Blog {
        val emojis = this.emojis.map(emojiEntityAdapter::toEmoji)
        return Blog(
            id = id,
            author = activityPubAccountEntityAdapter.toAuthor(account),
            title = null,
            content = mapCustomEmoji(content, emojis),
            sensitive = sensitive,
            spoilerText = mapCustomEmoji(spoilerText, emojis),
            date = formatDatetimeToDate(createdAt),
            forwardCount = reblogsCount,
            likeCount = favouritesCount,
            repliesCount = repliesCount,
            platform = platform,
            mediaList = mediaAttachments?.map { it.toBlogMedia() } ?: emptyList(),
            poll = poll?.let(pollAdapter::adapt),
            emojis = emojis,
            mentions = this.mentions.mapNotNull { it.toMention() }
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
        val webFinger = WebFinger.create(acct) ?: return null
        return Mention(
            id = id,
            username = username,
            url = url,
            webFinger = webFinger,
        )
    }
}
