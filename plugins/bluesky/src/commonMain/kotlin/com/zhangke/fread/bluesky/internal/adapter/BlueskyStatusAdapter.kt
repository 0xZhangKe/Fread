package com.zhangke.fread.bluesky.internal.adapter

import app.bsky.embed.AspectRatio
import app.bsky.embed.ImagesViewImage
import app.bsky.embed.RecordWithMediaViewMediaUnion
import app.bsky.embed.VideoView
import app.bsky.feed.FeedViewPostReasonUnion
import app.bsky.feed.PostViewEmbedUnion
import com.zhangke.framework.datetime.Instant
import com.zhangke.fread.bluesky.internal.model.ProcessingBskyPost
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaMeta
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.blog.PreviewCard
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
import kotlinx.collections.immutable.persistentListOf
import me.tatarka.inject.annotations.Inject

class BlueskyStatusAdapter @Inject constructor(
    private val accountAdapter: BlueskyAccountAdapter,
) {

    fun convert(
        processingBskyPost: ProcessingBskyPost,
        supportInteraction: List<StatusInteraction>,
        platform: BlogPlatform,
        isSelfStatus: Boolean,
    ): Status {
        val feedsReason = processingBskyPost.reason
        val pinned = feedsReason is FeedViewPostReasonUnion.ReasonPin
        if (feedsReason is FeedViewPostReasonUnion.ReasonRepost) {
            val author = accountAdapter.convertToBlogAuthor(feedsReason.value.by)
            val repostedStatus = convertToBlog(processingBskyPost, platform, pinned, isSelfStatus)
            return Status.Reblog(
                id = repostedStatus.id,
                datetime = repostedStatus.date.instant.toEpochMilliseconds(),
                author = author,
                reblog = repostedStatus,
                supportInteraction = supportInteraction,
            )
        }
        return Status.NewBlog(
            blog = convertToBlog(processingBskyPost, platform, pinned, isSelfStatus),
            supportInteraction = supportInteraction,
        )
    }

    private fun convertToBlog(
        processingBskyPost: ProcessingBskyPost,
        platform: BlogPlatform,
        pinned: Boolean,
        isSelfStatus: Boolean,
    ): Blog {
        val post = processingBskyPost.post
        val postView = processingBskyPost.postView
        return Blog(
            id = postView.cid.cid,
            author = accountAdapter.convertToBlogAuthor(postView.author),
            title = null,
            description = null,
            content = post.text,
            url = postView.uri.atUri,
            date = Instant(post.createdAt),
            forwardCount = postView.repostCount,
            likeCount = postView.likeCount,
            repliesCount = postView.replyCount,
            sensitive = false,
            spoilerText = "",
            language = post.langs.firstOrNull()?.tag,
            platform = platform,
            mediaList = postView.embed?.let(::convertToMedia) ?: emptyList(),
            emojis = emptyList(),
            mentions = emptyList(),
            tags = emptyList(),
            pinned = pinned,
            poll = null,
            visibility = StatusVisibility.PUBLIC,
            card = convertPreviewCard(postView.embed),
            isSelf = isSelfStatus,
            supportTranslate = false,
        )
    }

//    private fun convertToVisibility(threadGateView: ThreadgateView?): StatusVisibility {
//        val threadGate: Threadgate? = threadGateView?.record?.let { record ->
//            bskyJson.decodeFromJsonElement(bskyJson.encodeToJsonElement(record))
//        }
//        threadGate ?: return StatusVisibility.PUBLIC
//        if (threadGate.allow.isEmpty()) return StatusVisibility.PRIVATE
//        threadGate.allow.
//    }

    private fun convertToMedia(embedUnion: PostViewEmbedUnion): List<BlogMedia> {
        return when (embedUnion) {
            is PostViewEmbedUnion.ImagesView -> {
                embedUnion.value.images.map { it.toMedia() }
            }

            is PostViewEmbedUnion.VideoView -> {
                listOf(embedUnion.value.toMedia())
            }

            is PostViewEmbedUnion.RecordWithMediaView -> {
                when (val media = embedUnion.value.media) {
                    is RecordWithMediaViewMediaUnion.ExternalView -> persistentListOf()
                    is RecordWithMediaViewMediaUnion.ImagesView -> {
                        media.value.images.map { it.toMedia() }
                    }

                    is RecordWithMediaViewMediaUnion.VideoView -> {
                        listOf(media.value.toMedia())
                    }
                }
            }

            else -> emptyList()
        }
    }

    private fun ImagesViewImage.toMedia(): BlogMedia {
        return BlogMedia(
            id = this.fullsize.uri,
            url = this.fullsize.uri,
            type = BlogMediaType.IMAGE,
            previewUrl = this.thumb.uri,
            remoteUrl = null,
            description = this.alt,
            blurhash = null,
            meta = aspectRatio?.let(::buildImageMediaMeta),
        )
    }

    private fun buildImageMediaMeta(aspectRatio: AspectRatio): BlogMediaMeta.ImageMeta {
        return BlogMediaMeta.ImageMeta(
            original = BlogMediaMeta.ImageMeta.LayoutMeta(
                width = aspectRatio.width,
                height = aspectRatio.height,
                size = null,
                aspect = aspectRatio.aspect,
            ),
            small = null,
            focus = null,
        )
    }

    private fun VideoView.toMedia(): BlogMedia {
        return BlogMedia(
            id = this.playlist.uri,
            url = this.playlist.uri,
            type = BlogMediaType.VIDEO,
            previewUrl = this.thumbnail?.uri,
            remoteUrl = null,
            description = this.alt,
            blurhash = null,
            meta = aspectRatio?.let(::buildImageVideoMeta),
        )
    }

    private fun buildImageVideoMeta(aspectRatio: AspectRatio): BlogMediaMeta.VideoMeta {
        return BlogMediaMeta.VideoMeta(
            length = null,
            duration = null,
            fps = null,
            size = null,
            width = aspectRatio.width,
            height = aspectRatio.height,
            aspect = aspectRatio.aspect,
            audioEncode = null,
            audioBitrate = null,
            audioChannels = null,
            original = BlogMediaMeta.VideoMeta.LayoutMeta(
                width = aspectRatio.width,
                height = aspectRatio.height,
                size = null,
                aspect = aspectRatio.aspect,
                frameRate = null,
                duration = null,
                bitrate = null,
            ),
            small = null,
        )
    }

    private val AspectRatio.aspect: Float
        get() = (width.toDouble() / height.toDouble()).toFloat()

    private fun convertPreviewCard(embed: PostViewEmbedUnion?): PreviewCard? {
        val externalView =
            (embed as? PostViewEmbedUnion.ExternalView)?.value?.external ?: return null
        return PreviewCard(
            type = if (externalView.thumb?.uri.isNullOrEmpty()) {
                PreviewCard.CardType.LINK
            } else {
                PreviewCard.CardType.PHOTO
            },
            url = externalView.uri.uri,
            title = externalView.title,
            description = externalView.description,
        )
    }
}
