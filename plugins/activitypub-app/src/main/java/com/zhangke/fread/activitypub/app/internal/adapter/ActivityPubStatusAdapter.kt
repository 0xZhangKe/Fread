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
import com.zhangke.fread.status.blog.PreviewCard
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusVisibility
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

    private fun ActivityPubStatusEntity.toBlog(platform: BlogPlatform): Blog {
        val emojis = this.emojis.map(emojiEntityAdapter::toEmoji)
        return Blog(
            id = id,
            author = activityPubAccountEntityAdapter.toAuthor(account),
            title = null,
            description = null,
            content = content.orEmpty(),
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
            pinned = pinned ?: false,
            mentions = this.mentions.mapNotNull { it.toMention() },
            tags = tags.map { it.toTag() },
            visibility = visibility.convertActivityPubVisibility(),
            card = card?.toCard(),
            editedAt = editedAt?.let { formatDatetimeToDate(it) },
        )
    }

    private fun String.convertActivityPubVisibility(): StatusVisibility {
        return when (this) {
            ActivityPubStatusEntity.VISIBILITY_PUBLIC -> StatusVisibility.PUBLIC
            ActivityPubStatusEntity.VISIBILITY_UNLISTED -> StatusVisibility.UNLISTED
            ActivityPubStatusEntity.VISIBILITY_PRIVATE -> StatusVisibility.PRIVATE
            ActivityPubStatusEntity.VISIBILITY_DIRECT -> StatusVisibility.DIRECT
            else -> StatusVisibility.PUBLIC
        }
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

    private fun ActivityPubStatusEntity.PreviewCard.toCard(): PreviewCard {
        return PreviewCard(
            type = type.convertCardType(),
            url = url,
            title = title,
            description = description,
            authorName = authorName,
            authorUrl = authorUrl,
            providerName = providerName,
            providerUrl = providerUrl,
            html = html,
            width = width,
            height = height,
            image = image,
            embedUrl = embedUrl,
            blurhash = blurhash,
        )
    }

    private fun String.convertCardType(): PreviewCard.CardType {
        return when (this) {
            ActivityPubStatusEntity.PreviewCard.TYPE_LINK -> PreviewCard.CardType.LINK
            ActivityPubStatusEntity.PreviewCard.TYPE_PHOTO -> PreviewCard.CardType.PHOTO
            ActivityPubStatusEntity.PreviewCard.TYPE_VIDEO -> PreviewCard.CardType.VIDEO
            ActivityPubStatusEntity.PreviewCard.TYPE_RICH -> PreviewCard.CardType.RICH
            else -> PreviewCard.CardType.PHOTO
        }
    }
}
