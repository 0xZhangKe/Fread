package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubMediaAttachmentEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaType
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusInteraction
import javax.inject.Inject

class ActivityPubStatusAdapter @Inject constructor(
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val activityPubAccountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val metaAdapter: ActivityPubBlogMetaAdapter,
    private val pollAdapter: ActivityPubPollAdapter,
) {

    fun toStatus(
        entity: ActivityPubStatusEntity,
        supportActions: List<StatusInteraction>,
    ): Status {
        return if (entity.reblog != null) {
            entity.toReblog(supportActions)
        } else {
            entity.toNewBlog(supportActions)
        }
    }

    private fun ActivityPubStatusEntity.toNewBlog(supportActions: List<StatusInteraction>): Status.NewBlog {
        return Status.NewBlog(toBlog(), supportActions)
    }

    private fun ActivityPubStatusEntity.toReblog(supportActions: List<StatusInteraction>): Status.Reblog {
        return Status.Reblog(
            author = activityPubAccountEntityAdapter.toAuthor(account),
            id = id,
            datetime = formatDatetimeToDate(createdAt).time,
            reblog = reblog!!.toBlog(),
            supportInteraction = supportActions,
        )
    }

    private fun ActivityPubStatusEntity.toBlog(): Blog {
        return Blog(
            id = id,
            author = activityPubAccountEntityAdapter.toAuthor(account),
            title = null,
            content = content,
            sensitive = sensitive,
            spoilerText = spoilerText,
            date = formatDatetimeToDate(createdAt),
            forwardCount = reblogsCount,
            likeCount = favouritesCount,
            repliesCount = repliesCount,
            mediaList = mediaAttachments?.map { it.toBlogMedia() } ?: emptyList(),
            poll = poll?.let(pollAdapter::adapt)
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
}
