package com.zhangke.fread.bluesky.internal.adapter

import app.bsky.actor.ProfileViewBasic
import app.bsky.embed.AspectRatio
import app.bsky.embed.ExternalViewExternal
import app.bsky.embed.ImagesViewImage
import app.bsky.embed.RecordViewRecord
import app.bsky.embed.RecordViewRecordUnion
import app.bsky.embed.RecordWithMediaViewMediaUnion
import app.bsky.embed.VideoView
import app.bsky.feed.FeedViewPostReasonUnion
import app.bsky.feed.Post
import app.bsky.feed.PostViewEmbedUnion
import com.zhangke.framework.datetime.Instant
import com.zhangke.fread.bluesky.internal.model.ProcessingBskyPost
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaMeta
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
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
            val repostedStatus = convertToBlog(
                processingBskyPost,
                supportInteraction,
                platform,
                pinned,
                isSelfStatus,
            )
            return Status.Reblog(
                id = repostedStatus.id,
                datetime = repostedStatus.date.instant.toEpochMilliseconds(),
                author = author,
                reblog = repostedStatus,
                supportInteraction = supportInteraction,
            )
        }
        return Status.NewBlog(
            blog = convertToBlog(
                processingBskyPost,
                supportInteraction,
                platform,
                pinned,
                isSelfStatus,
            ),
            supportInteraction = supportInteraction,
        )
    }

    private fun convertToBlog(
        processingBskyPost: ProcessingBskyPost,
        supportInteraction: List<StatusInteraction>,
        platform: BlogPlatform,
        pinned: Boolean,
        isSelfStatus: Boolean,
    ): Blog {
        return convertToBlog(
            post = processingBskyPost.post,
            supportInteraction = supportInteraction,
            id = processingBskyPost.postView.cid.cid,
            author = processingBskyPost.postView.author,
            url = processingBskyPost.postView.uri.atUri,
            replyCount = processingBskyPost.postView.replyCount,
            likeCount = processingBskyPost.postView.likeCount,
            repostCount = processingBskyPost.postView.repostCount,
            liked = supportInteraction.liked,
            bookmarked = supportInteraction.bookmarked,
            forward = supportInteraction.forwarded,
            embedUnion = processingBskyPost.postView.embed,
            platform = platform,
            pinned = pinned,
            isSelfStatus = isSelfStatus,
        )
    }

    private fun convertToBlog(
        recordView: RecordViewRecord,
        supportInteraction: List<StatusInteraction>,
        platform: BlogPlatform,
    ): Blog {
        val post: Post =
            bskyJson.decodeFromJsonElement(bskyJson.encodeToJsonElement(recordView.value))
        return convertToBlog(
            post = post,
            supportInteraction = supportInteraction,
            id = recordView.cid.cid,
            author = recordView.author,
            url = recordView.uri.atUri,
            replyCount = recordView.replyCount,
            likeCount = recordView.likeCount,
            repostCount = recordView.repostCount,
            liked = supportInteraction.liked,
            bookmarked = supportInteraction.bookmarked,
            forward = supportInteraction.forwarded,
            embedUnion = null,
            platform = platform,
            pinned = false,
            isSelfStatus = false,
        )
    }

    private fun convertToBlog(
        post: Post,
        supportInteraction: List<StatusInteraction>,
        id: String,
        author: ProfileViewBasic,
        url: String,
        repostCount: Long?,
        likeCount: Long?,
        replyCount: Long?,
        liked: Boolean,
        bookmarked: Boolean,
        forward: Boolean,
        embedUnion: PostViewEmbedUnion?,
        platform: BlogPlatform,
        pinned: Boolean,
        isSelfStatus: Boolean,
    ): Blog {
        return Blog(
            id = id,
            author = accountAdapter.convertToBlogAuthor(author),
            title = null,
            description = null,
            content = post.text,
            url = url,
            date = Instant(post.createdAt),
            forwardCount = repostCount,
            likeCount = likeCount,
            repliesCount = replyCount,
            liked = liked,
            bookmarked = bookmarked,
            forward = forward,
            sensitive = false,
            spoilerText = "",
            language = post.langs.firstOrNull()?.tag,
            platform = platform,
            mediaList = embedUnion?.let(::convertToMedia) ?: emptyList(),
            emojis = emptyList(),
            mentions = emptyList(),
            tags = emptyList(),
            pinned = pinned,
            poll = null,
            visibility = StatusVisibility.PUBLIC,
            embeds = convertEmbed(embedUnion, supportInteraction, platform),
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
                    // ExternalView will be convert to link embed
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

    private fun convertEmbed(
        embedUnion: PostViewEmbedUnion?,
        supportInteraction: List<StatusInteraction>,
        platform: BlogPlatform,
    ): List<BlogEmbed> {
        if (embedUnion is PostViewEmbedUnion.ExternalView) {
            return listOf(embedUnion.value.external.toLinkEmbed())
        }
        if (embedUnion is PostViewEmbedUnion.RecordWithMediaView) {
            val embeds = mutableListOf<BlogEmbed>()
            val media = embedUnion.value.media
            if (media is RecordWithMediaViewMediaUnion.ExternalView) {
                // another type will be convert to media list in Blog
                embeds += media.value.external.toLinkEmbed()
            }
            val record = embedUnion.value.record.record
            if (record is RecordViewRecordUnion.ViewRecord) {
                embeds += record.toBlogEmbed(supportInteraction, platform)
            }
            return embeds
        }
        if (embedUnion is PostViewEmbedUnion.RecordView) {
            val embedRecord = embedUnion.value.record
            if (embedRecord is RecordViewRecordUnion.ViewRecord) {
                return listOf(embedRecord.toBlogEmbed(supportInteraction, platform))
            }
        }
        return emptyList()
    }

    private fun RecordViewRecordUnion.ViewRecord.toBlogEmbed(
        supportInteraction: List<StatusInteraction>,
        platform: BlogPlatform,
    ): BlogEmbed {
        return BlogEmbed.Blog(convertToBlog(this.value, supportInteraction, platform))
    }

    private fun ExternalViewExternal.toLinkEmbed(): BlogEmbed.Link {
        return BlogEmbed.Link(
            url = this.uri.uri,
            title = this.title,
            description = this.description,
            image = this.thumb?.uri,
            video = false,
        )
    }

    private val List<StatusInteraction>.liked: Boolean
        get() = filterIsInstance<StatusInteraction.Like>().firstOrNull()?.liked ?: false

    private val List<StatusInteraction>.forwarded: Boolean
        get() = filterIsInstance<StatusInteraction.Forward>().firstOrNull()?.forwarded ?: false

    private val List<StatusInteraction>.bookmarked: Boolean
        get() = filterIsInstance<StatusInteraction.Bookmark>().firstOrNull()?.bookmarked ?: false
}
