package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubMediaAttachmentEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.datetime.Instant
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.blog.PostingApplication
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
import me.tatarka.inject.annotations.Inject

class ActivityPubStatusAdapter @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val getStatusSupportInteraction: GetStatusInteractionUseCase,
    private val activityPubAccountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val metaAdapter: ActivityPubBlogMetaAdapter,
    private val pollAdapter: ActivityPubPollAdapter,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) {

    suspend fun toStatus(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
    ): Status {
        return if (entity.reblog != null) {
            transformReblog(entity, platform)
        } else {
            transformNewBlog(entity, platform)
        }
    }

    private suspend fun transformNewBlog(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
    ): Status.NewBlog {
        val (blog, supportActions) = getBlogAndInteractions(entity, platform)
        return Status.NewBlog(blog, supportActions)
    }

    private suspend fun transformReblog(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
    ): Status.Reblog {
        val (blog, supportActions) = getBlogAndInteractions(entity.reblog!!, platform)
        return Status.Reblog(
            author = activityPubAccountEntityAdapter.toAuthor(entity.account),
            id = entity.id,
            datetime = formatDatetimeToDate(entity.createdAt).toEpochMilliseconds(),
            reblog = blog,
            supportInteraction = supportActions,
        )
    }

    private suspend fun getBlogAndInteractions(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform
    ): Pair<Blog, List<StatusInteraction>> {
        val currentLoginAccount = accountManager.getAllLoggedAccount()
            .firstOrNull { it.platform.baseUrl.equalsDomain(platform.baseUrl) }
        val statusAuthor = activityPubAccountEntityAdapter.toAuthor(entity.account)
        val isSelfStatus =
            currentLoginAccount?.webFinger?.equalsDomain(statusAuthor.webFinger) == true
        val blog = transformBlog(entity, platform, statusAuthor, isSelfStatus)
        val supportActions = getStatusSupportInteraction(
            entity = entity,
            isSelfStatus = isSelfStatus,
            logged = currentLoginAccount != null,
        )
        return blog to supportActions
    }

    private suspend fun transformBlog(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
        author: BlogAuthor,
        isSelfStatus: Boolean,
    ): Blog {
        val emojis = entity.emojis.map(emojiEntityAdapter::toEmoji)
        return Blog(
            id = entity.id,
            author = author,
            title = null,
            description = null,
            content = entity.content.orEmpty(),
            sensitive = entity.sensitive,
            spoilerText = entity.spoilerText,
            date = Instant(formatDatetimeToDate(entity.createdAt)),
            url = entity.url.ifNullOrEmpty { entity.uri },
            language = entity.language,
            forwardCount = entity.reblogsCount.toLong(),
            likeCount = entity.favouritesCount.toLong(),
            repliesCount = entity.repliesCount.toLong(),
            platform = platform,
            mediaList = entity.mediaAttachments?.map { it.toBlogMedia() } ?: emptyList(),
            poll = entity.poll?.let(pollAdapter::adapt),
            emojis = emojis,
            pinned = entity.pinned ?: false,
            isSelf = isSelfStatus,
            supportTranslate = true,
            mentions = entity.mentions.mapNotNull { it.toMention() },
            tags = entity.tags.map { it.toTag() },
            visibility = entity.visibility.convertActivityPubVisibility(),
            embeds = entity.card?.toEmbed()?.let { listOf(it) } ?: emptyList(),
            editedAt = entity.editedAt?.let { formatDatetimeToDate(it) }?.let { Instant(it) },
            application = entity.application?.toApplication(),
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
            url = url.orEmpty(),
            type = mediaType,
            previewUrl = previewUrl,
            remoteUrl = remoteUrl,
            description = description,
            meta = this.meta?.let { metaAdapter.adapt(mediaType, it) },
            blurhash = blurhash,
        )
    }

    private suspend fun ActivityPubStatusEntity.Tag.toTag(): HashtagInStatus {
        return HashtagInStatus(
            name = name,
            url = url,
            protocol = createActivityPubProtocol(),
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

    private suspend fun ActivityPubStatusEntity.Mention.toMention(): Mention? {
        val webFinger = WebFinger.create(acct) ?: WebFinger.create(this.url) ?: return null
        return Mention(
            id = id,
            username = username,
            url = url,
            webFinger = webFinger,
            protocol = createActivityPubProtocol(),
        )
    }

    private fun ActivityPubStatusEntity.PreviewCard.toEmbed(): BlogEmbed {
        return BlogEmbed.Link(
            url = url,
            title = title,
            description = description,
            video = type == ActivityPubStatusEntity.PreviewCard.TYPE_VIDEO,
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

    private fun ActivityPubStatusEntity.Application.toApplication(): PostingApplication {
        return PostingApplication(
            name = name,
            website = website,
        )
    }
}
