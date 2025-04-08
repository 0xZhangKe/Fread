package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubMediaAttachmentEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.common.utils.formatDefault
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.blog.PostingApplication
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject

class ActivityPubStatusAdapter @Inject constructor(
    private val activityPubAccountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val metaAdapter: ActivityPubBlogMetaAdapter,
    private val pollAdapter: ActivityPubPollAdapter,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) {

    fun toStatusUiState(
        status: Status,
        role: IdentityRole,
        logged: Boolean,
        isOwner: Boolean,
    ): StatusUiState {
        return StatusUiState(
            status = status,
            role = role,
            logged = logged,
            isOwner = isOwner,
            blogTranslationState = BlogTranslationUiState(
                support = status.intrinsicBlog.supportTranslate,
                translating = false,
                showingTranslation = false,
                blogTranslation = null,
            ),
        )
    }

    fun toStatusUiState(
        status: Status,
        role: IdentityRole,
        loggedAccount: ActivityPubLoggedAccount?,
    ): StatusUiState {
        return toStatusUiState(
            status = status,
            role = role,
            logged = loggedAccount != null,
            isOwner = loggedAccount?.webFinger?.equalsDomain(status.intrinsicBlog.author.webFinger) == true,
        )
    }

    suspend fun toStatusUiState(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
        role: IdentityRole,
        loggedAccount: ActivityPubLoggedAccount?,
    ): StatusUiState {
        val status = toStatus(entity, platform)
        return toStatusUiState(status, role, loggedAccount)
    }

    suspend fun toStatusUiState(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
        role: IdentityRole,
        isOwner: Boolean,
        logged: Boolean,
    ): StatusUiState {
        val status = toStatus(entity, platform)
        return toStatusUiState(status, role, logged = logged, isOwner = isOwner)
    }

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
        val blog = getBlogAndInteractions(entity, platform)
        return Status.NewBlog(blog)
    }

    private suspend fun transformReblog(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
    ): Status.Reblog {
        val blog = getBlogAndInteractions(entity.reblog!!, platform)
        return Status.Reblog(
            author = activityPubAccountEntityAdapter.toAuthor(entity.account),
            id = entity.id,
            createAt = DateParser.parseOrCurrent(entity.createdAt),
            reblog = blog,
        )
    }

    private suspend fun getBlogAndInteractions(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform
    ): Blog {
        val statusAuthor = activityPubAccountEntityAdapter.toAuthor(entity.account)
        return transformBlog(entity, platform, statusAuthor)
    }

    private suspend fun transformBlog(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
        author: BlogAuthor,
    ): Blog {
        val emojis = entity.emojis.map(emojiEntityAdapter::toEmoji)
        val createAt = DateParser.parseOrCurrent(entity.createdAt)
        return Blog(
            id = entity.id,
            author = author,
            title = null,
            description = null,
            content = entity.content.orEmpty(),
            sensitive = entity.sensitive,
            spoilerText = entity.spoilerText,
            createAt = createAt,
            formattedCreateAt = createAt.formatDefault(),
            url = entity.url.ifNullOrEmpty { entity.uri },
            link = entity.url.ifNullOrEmpty { entity.uri },
            language = entity.language,
            like = Blog.Like(
                support = true,
                liked = entity.favourited,
                likedCount = entity.favouritesCount.toLong()
            ),
            forward = Blog.Forward(
                support = true,
                forward = entity.reblogged,
                forwardCount = entity.reblogsCount.toLong(),
            ),
            bookmark = Blog.Bookmark(
                support = true,
            ),
            reply = Blog.Reply(
                support = true,
                repliesCount = entity.repliesCount.toLong(),
            ),
            supportEdit = true,
            quote = Blog.Quote(support = false),
            platform = platform,
            mediaList = entity.mediaAttachments?.map { it.toBlogMedia() } ?: emptyList(),
            poll = entity.poll?.let(pollAdapter::adapt),
            emojis = emojis,
            pinned = entity.pinned == true,
            facets = emptyList(),
            supportTranslate = true,
            mentions = entity.mentions.mapNotNull { it.toMention() },
            tags = entity.tags.map { it.toTag() },
            visibility = entity.visibility.convertActivityPubVisibility(),
            embeds = entity.card?.toEmbed()?.let { listOf(it) } ?: emptyList(),
            editedAt = entity.editedAt?.let { DateParser.parseOrCurrent(it) },
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
