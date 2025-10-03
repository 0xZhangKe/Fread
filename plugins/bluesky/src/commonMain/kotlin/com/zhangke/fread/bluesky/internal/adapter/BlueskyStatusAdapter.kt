package com.zhangke.fread.bluesky.internal.adapter

import app.bsky.embed.AspectRatio
import app.bsky.embed.ExternalViewExternal
import app.bsky.embed.ImagesViewImage
import app.bsky.embed.RecordViewRecord
import app.bsky.embed.RecordViewRecordUnion
import app.bsky.embed.RecordWithMediaViewMediaUnion
import app.bsky.embed.VideoView
import app.bsky.feed.FeedViewPost
import app.bsky.feed.FeedViewPostReasonUnion
import app.bsky.feed.Post
import app.bsky.feed.PostView
import app.bsky.feed.PostViewEmbedUnion
import app.bsky.richtext.Facet
import app.bsky.richtext.FacetFeatureUnion
import com.zhangke.framework.datetime.Instant
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.common.utils.formatDefault
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaMeta
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import me.tatarka.inject.annotations.Inject

class BlueskyStatusAdapter @Inject constructor(
    private val accountAdapter: BlueskyAccountAdapter,
) {

    fun convert(
        postView: PostView,
        platform: BlogPlatform,
        pinned: Boolean = false,
    ): Status {
        return Status.NewBlog(
            blog = convertToBlog(
                postView = postView,
                platform = platform,
                pinned = pinned,
            ),
        )
    }

    fun convert(
        feedViewPost: FeedViewPost,
        platform: BlogPlatform,
    ): Status {
        val feedsReason = feedViewPost.reason
        val pinned = feedsReason is FeedViewPostReasonUnion.ReasonPin
        val blog = convertToBlog(
            postView = feedViewPost.post,
            platform = platform,
            pinned = pinned,
        )
        if (feedsReason is FeedViewPostReasonUnion.ReasonRepost) {
            val author = accountAdapter.convertToBlogAuthor(feedsReason.value.by)
            return Status.Reblog(
                id = blog.id,
                createAt = Instant(blog.createAt.instant),
                author = author,
                reblog = blog,
            )
        }
        return Status.NewBlog(blog)
    }

    fun convertToUiState(
        locator: PlatformLocator,
        status: Status,
        logged: Boolean,
        isOwner: Boolean,
    ): StatusUiState {
        return StatusUiState(
            status = status,
            logged = logged,
            isOwner = isOwner,
            blogTranslationState = BlogTranslationUiState(support = false),
            locator = locator,
        )
    }

    fun convertToUiState(
        locator: PlatformLocator,
        postView: PostView,
        platform: BlogPlatform,
        loggedAccount: BlueskyLoggedAccount?,
        pinned: Boolean = false,
    ): StatusUiState {
        val status = convert(postView, platform, pinned)
        return convertToUiState(
            locator = locator,
            status = status,
            logged = loggedAccount != null,
            isOwner = loggedAccount != null && postView.author.did.did == loggedAccount.did,
        )
    }

    fun convertToUiState(
        locator: PlatformLocator,
        feedViewPost: FeedViewPost,
        platform: BlogPlatform,
        loggedAccount: BlueskyLoggedAccount?,
    ): StatusUiState {
        val status = convert(
            feedViewPost = feedViewPost,
            platform = platform,
        )
        return convertToUiState(
            locator = locator,
            status = status,
            logged = loggedAccount != null,
            isOwner = loggedAccount != null && feedViewPost.post.author.did.did == loggedAccount.did,
        )
    }

    private fun convertToBlog(
        postView: PostView,
        platform: BlogPlatform,
        pinned: Boolean,
    ): Blog {
        val post: Post = postView.record.bskyJson()
        return convertToBlog(
            post = post,
            id = postView.cid.cid,
            author = accountAdapter.convertToBlogAuthor(postView.author),
            url = postView.uri.atUri,
            replyCount = postView.replyCount,
            likeCount = postView.likeCount,
            repostCount = postView.repostCount,
            liked = postView.viewer?.like?.atUri.isNullOrEmpty().not(),
            forward = postView.viewer?.repost?.atUri.isNullOrEmpty().not(),
            mediaList = postView.embed?.let(::convertToMedia) ?: emptyList(),
            embedList = convertEmbed(postView.embed, platform),
            platform = platform,
            pinned = pinned,
        )
    }

    private fun convertToBlog(
        recordView: RecordViewRecord,
        platform: BlogPlatform,
    ): Blog {
        val post: Post =
            bskyJson.decodeFromJsonElement(bskyJson.encodeToJsonElement(recordView.value))
        return convertToBlog(
            post = post,
            id = recordView.cid.cid,
            author = accountAdapter.convertToBlogAuthor(recordView.author),
            url = recordView.uri.atUri,
            repostCount = recordView.repostCount,
            likeCount = recordView.likeCount,
            replyCount = recordView.replyCount,
            platform = platform,
        )
    }

    fun convertToBlog(
        post: Post,
        id: String,
        author: BlogAuthor,
        url: String,
        platform: BlogPlatform,
        liked: Boolean = false,
        forward: Boolean = false,
        pinned: Boolean = false,
        repostCount: Long? = null,
        likeCount: Long? = null,
        replyCount: Long? = null,
        embedList: List<BlogEmbed> = emptyList(),
        mediaList: List<BlogMedia> = emptyList(),
    ): Blog {
        val createAt = Instant(post.createdAt)
        return Blog(
            id = id,
            author = author,
            title = null,
            description = null,
            content = post.text,
            url = url,
            link = buildLink(
                url = url,
                handle = author.handle,
            ),
            createAt = createAt,
            formattedCreateAt = createAt.formatDefault(),
            like = Blog.Like(
                support = true,
                liked = liked,
                likedCount = likeCount ?: 0L,
            ),
            forward = Blog.Forward(
                support = true,
                forward = forward,
                forwardCount = repostCount ?: 0L,
            ),
            bookmark = Blog.Bookmark(
                support = false,
            ),
            reply = Blog.Reply(
                support = true,
                repliesCount = replyCount ?: 0L,
            ),
            quote = Blog.Quote(support = true, enabled = true),
            supportEdit = false,
            sensitive = false,
            spoilerText = "",
            isReply = post.reply != null,
            language = post.langs.firstOrNull()?.tag,
            platform = platform,
            mediaList = mediaList,
            emojis = emptyList(),
            mentions = emptyList(),
            tags = emptyList(),
            pinned = pinned,
            poll = null,
            facets = post.facets.map { it.convert() },
            visibility = StatusVisibility.PUBLIC,
            embeds = embedList,
            supportTranslate = false,
        )
    }

    private fun buildLink(
        url: String,
        handle: String,
    ): String {
        //https://bsky.app/profile/lotuscat.bsky.social/post/3llqkin45722p
        return buildString {
            append("https://bsky.app/profile/")
            append(handle.removePrefix("@"))
            append("/post/")
            append(url.substringAfterLast("/"))
        }
    }

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
                    is RecordWithMediaViewMediaUnion.ExternalView -> emptyList()
                    is RecordWithMediaViewMediaUnion.ImagesView -> {
                        media.value.images.map { it.toMedia() }
                    }

                    is RecordWithMediaViewMediaUnion.VideoView -> {
                        listOf(media.value.toMedia())
                    }

                    is RecordWithMediaViewMediaUnion.Unknown -> emptyList()
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
                embeds += record.toBlogEmbed(platform)
            }
            return embeds
        }
        if (embedUnion is PostViewEmbedUnion.RecordView) {
            val embedRecord = embedUnion.value.record
            if (embedRecord is RecordViewRecordUnion.ViewRecord) {
                return listOf(embedRecord.toBlogEmbed(platform))
            }
        }
        return emptyList()
    }

    private fun RecordViewRecordUnion.ViewRecord.toBlogEmbed(
        platform: BlogPlatform,
    ): BlogEmbed {
        return BlogEmbed.Blog(convertToBlog(this.value, platform))
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

    private fun Facet.convert(): com.zhangke.fread.status.model.Facet {
        return com.zhangke.fread.status.model.Facet(
            byteStart = this.index.byteStart,
            byteEnd = this.index.byteEnd,
            features = this.features.mapNotNull { feature ->
                when (feature) {
                    is FacetFeatureUnion.Mention -> com.zhangke.fread.status.model.FacetFeatureUnion.Mention(
                        did = feature.value.did.did,
                    )

                    is FacetFeatureUnion.Link -> com.zhangke.fread.status.model.FacetFeatureUnion.Link(
                        uri = feature.value.uri.uri,
                    )

                    is FacetFeatureUnion.Tag -> com.zhangke.fread.status.model.FacetFeatureUnion.Tag(
                        tag = feature.value.tag,
                    )

                    is FacetFeatureUnion.Unknown -> null
                }
            }
        )
    }
}
