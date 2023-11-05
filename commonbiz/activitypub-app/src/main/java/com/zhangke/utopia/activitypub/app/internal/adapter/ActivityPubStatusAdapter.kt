package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entry.ActivityPubMediaAttachmentEntity
import com.zhangke.activitypub.entry.ActivityPubStatusEntity
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.activitypub.app.internal.user.ActivityPubUserAdapter
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaType
import com.zhangke.utopia.status.status.Status
import javax.inject.Inject

class ActivityPubStatusAdapter @Inject constructor(
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val activityPubUserAdapter: ActivityPubUserAdapter,
    private val metaAdapter: ActivityPubBlogMetaAdapter,
    private val pollAdapter: ActivityPubPollAdapter,
) {

    fun adapt(entity: ActivityPubStatusEntity): Status {
        //fixme temporary code
        return try {
            Status.NewBlog(entity.toBlog())
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }

    private fun ActivityPubStatusEntity.toBlog(): Blog {
        return Blog(
            id = id,
            author = activityPubUserAdapter.adapt(
                account,
            ),
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
