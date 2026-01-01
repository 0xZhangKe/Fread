package com.zhangke.fread.bluesky.internal.usecase

import androidx.compose.ui.text.TextRange
import app.bsky.actor.ProfileView
import app.bsky.embed.AspectRatio
import app.bsky.embed.Images
import app.bsky.embed.ImagesImage
import app.bsky.embed.Record
import app.bsky.embed.RecordWithMedia
import app.bsky.embed.RecordWithMediaMediaUnion
import app.bsky.embed.Video
import app.bsky.feed.GetPostThreadQueryParams
import app.bsky.feed.GetPostThreadResponseThreadUnion
import app.bsky.feed.Post
import app.bsky.feed.PostEmbedUnion
import app.bsky.feed.PostReplyRef
import app.bsky.feed.PostView
import app.bsky.feed.Postgate
import app.bsky.feed.PostgateDisableRule
import app.bsky.feed.PostgateEmbeddingRuleUnion
import app.bsky.feed.Threadgate
import app.bsky.feed.ThreadgateAllowUnion
import app.bsky.feed.ThreadgateFollowerRule
import app.bsky.feed.ThreadgateFollowingRule
import app.bsky.feed.ThreadgateListRule
import app.bsky.feed.ThreadgateMentionRule
import app.bsky.richtext.Facet
import app.bsky.richtext.FacetByteSlice
import app.bsky.richtext.FacetFeatureUnion
import app.bsky.richtext.FacetLink
import app.bsky.richtext.FacetMention
import app.bsky.richtext.FacetTag
import com.atproto.repo.ApplyWritesCreate
import com.atproto.repo.ApplyWritesRequest
import com.atproto.repo.ApplyWritesRequestWriteUnion
import com.atproto.repo.StrongRef
import com.zhangke.framework.utils.mapForErrorMessage
import com.zhangke.framework.utils.mapForMessage
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.adjustToRkey
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostMediaAttachment
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostMediaAttachmentFile
import com.zhangke.fread.bluesky.internal.utils.Tid
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.common.utils.HashtagTextUtils
import com.zhangke.fread.common.utils.LinkTextUtils
import com.zhangke.fread.common.utils.MentionTextUtil
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.PostInteractionSetting
import com.zhangke.fread.status.model.ReplySetting
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject
import okio.utf8Size
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Language
import sh.christian.ozone.api.RKey
import sh.christian.ozone.api.Uri

class PublishingPostUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val uploadBlob: UploadBlobUseCase,
) {

    suspend operator fun invoke(
        account: BlueskyLoggedAccount,
        content: String,
        selectedLanguages: List<String>,
        interactionSetting: PostInteractionSetting,
        replyBlog: Blog? = null,
        attachment: PublishPostMediaAttachment? = null,
        quoteBlog: Blog? = null,
        mentionedUsers: Set<ProfileView> = emptySet(),
    ): Result<Unit> {
        val client = clientManager.getClient(account.locator)
        val rkey = Tid.generateTID()
        val postUri = "at://${account.did}/${BskyCollections.feedPost.nsid}/$rkey"
        val embedResult = buildPostEmbed(account.locator, attachment, quoteBlog)
        if (embedResult.isFailure) return Result.failure(embedResult.exceptionOrNull()!!)
        val replyResult = buildReplyRef(account.locator, replyBlog)
        if (replyResult.isFailure) return Result.failure(replyResult.exceptionOrNull()!!)
        val post = Post(
            text = content,
            langs = selectedLanguages.map { Language(it) },
            embed = embedResult.getOrNull(),
            createdAt = Clock.System.now(),
            reply = replyResult.getOrNull(),
            facets = buildFacet(content, mentionedUsers),
        )
        val writes = mutableListOf<ApplyWritesRequestWriteUnion>()
        writes += ApplyWritesRequestWriteUnion.Create(
            ApplyWritesCreate(
                collection = BskyCollections.feedPost,
                value = post.bskyJson(),
                rkey = RKey(rkey),
            )
        )
        writes += buildTreadAndPostGate(AtUri(postUri), interactionSetting)
        val request = ApplyWritesRequest(
            repo = Did(account.did),
            validate = true,
            writes = writes,
        )
        return client.applyWritesCatching(request).map { }
    }

    private suspend fun buildReplyRef(
        locator: PlatformLocator,
        replyBlog: Blog?
    ): Result<PostReplyRef?> {
        val reply = replyBlog ?: return Result.success(null)
        val postView = getPostDetail(locator, reply.url).let {
            if (it.isFailure) {
                return Result.failure(
                    it.exceptionOrNull()!!.mapForMessage("Get reply post failed")
                )
            }
            it.getOrThrow()
        }
        val post: Post = postView.record.bskyJson()
        val replyPostRef = StrongRef(uri = postView.uri, cid = postView.cid)
        val root = post.reply?.root ?: replyPostRef
        return Result.success(
            PostReplyRef(root = root, parent = replyPostRef)
        )
    }

    private suspend fun getPostDetail(locator: PlatformLocator, uri: String): Result<PostView> {
        val client = clientManager.getClient(locator)
        val result = client.getPostThreadCatching(
            GetPostThreadQueryParams(uri = AtUri(uri), depth = 1)
        ).mapForErrorMessage("Get post detail failed")
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val response = result.getOrThrow()
        val threadPostView = (response.thread as? GetPostThreadResponseThreadUnion.ThreadViewPost)
            ?: return Result.failure(IllegalStateException("Post not found"))
        return Result.success(threadPostView.value.post)
    }

    private suspend fun buildPostEmbed(
        locator: PlatformLocator,
        attachment: PublishPostMediaAttachment?,
        quoteBlog: Blog?,
    ): Result<PostEmbedUnion?> {
        val videoResult =
            (attachment as? PublishPostMediaAttachment.Video)?.let { uploadVideo(locator, it.file) }
        if (videoResult?.isFailure == true) return Result.failure(videoResult.exceptionOrNull()!!)
        val imagesResult =
            (attachment as? PublishPostMediaAttachment.Image)?.let { uploadImages(locator, it) }
        if (imagesResult?.isFailure == true) return Result.failure(imagesResult.exceptionOrNull()!!)
        val video = videoResult?.getOrNull()
        val images = imagesResult?.getOrNull()
        val quoteRecord = quoteBlog
            ?.let { StrongRef(uri = AtUri(it.url), cid = Cid(it.id)) }
            ?.let { Record(it) }
        if (video == null && images == null && quoteRecord == null) return Result.success(null)
        val embed = if (quoteRecord != null) {
            if (video != null || images != null) {
                val media = video?.let { RecordWithMediaMediaUnion.Video(it) }
                    ?: RecordWithMediaMediaUnion.Images(images!!)
                PostEmbedUnion.RecordWithMedia(
                    RecordWithMedia(
                        record = quoteRecord,
                        media = media,
                    )
                )
            } else {
                PostEmbedUnion.Record(quoteRecord)
            }
        } else {
            video?.let { PostEmbedUnion.Video(it) } ?: PostEmbedUnion.Images(images!!)
        }
        return Result.success(embed)
    }

    private suspend fun uploadVideo(
        locator: PlatformLocator,
        file: PublishPostMediaAttachmentFile,
    ): Result<Video> {
        return uploadBlob(locator = locator, fileUri = file.file.uri)
            .map {
                Video(
                    video = it.first,
                    alt = file.alt,
                    aspectRatio = it.second?.convert(),
                )
            }
    }

    private suspend fun uploadImages(
        locator: PlatformLocator,
        image: PublishPostMediaAttachment.Image,
    ): Result<Images> {
        val resultList: List<Result<ImagesImage>> = supervisorScope {
            image.files.map { file ->
                async {
                    uploadBlob(locator = locator, fileUri = file.file.uri).map {
                        ImagesImage(
                            image = it.first,
                            aspectRatio = it.second?.convert(),
                            alt = file.alt.orEmpty(),
                        )
                    }
                }
            }.awaitAll()
        }
        if (resultList.any { it.isFailure }) {
            return Result.failure(resultList.first { it.isFailure }.exceptionOrNull()!!)
        }
        val images = Images(resultList.map { it.getOrThrow() })
        return Result.success(images)
    }

    private fun com.zhangke.framework.utils.AspectRatio.convert(): AspectRatio {
        return AspectRatio(
            width = width,
            height = height,
        )
    }

    private fun buildTreadAndPostGate(
        postUri: AtUri,
        interactionSetting: PostInteractionSetting,
    ): List<ApplyWritesRequestWriteUnion> {
        val list = mutableListOf<ApplyWritesRequestWriteUnion>()
        val replySetting = interactionSetting.replySetting
        if (replySetting !is ReplySetting.Everybody) {
            val allowList = mutableListOf<ThreadgateAllowUnion>()
            if (replySetting is ReplySetting.Combined) {
                allowList += buildThreadGateAllowList(replySetting)
            }
            val threadGate = Threadgate(
                post = postUri,
                allow = allowList,
                createdAt = Clock.System.now(),
            )
            list += ApplyWritesRequestWriteUnion.Create(
                ApplyWritesCreate(
                    collection = BskyCollections.threadGate,
                    value = threadGate.bskyJson(),
                    rkey = postUri.toString().adjustToRkey(),
                )
            )
        }
        if (!interactionSetting.allowQuote) {
            val postGate = Postgate(
                createdAt = Clock.System.now(),
                post = postUri,
                embeddingRules = listOf(PostgateEmbeddingRuleUnion.DisableRule(PostgateDisableRule)),
            )
            list += ApplyWritesRequestWriteUnion.Create(
                ApplyWritesCreate(
                    collection = BskyCollections.postGate,
                    value = postGate.bskyJson(),
                    rkey = postUri.toString().adjustToRkey(),
                )
            )
        }
        return list
    }

    private fun buildThreadGateAllowList(
        setting: ReplySetting.Combined,
    ): List<ThreadgateAllowUnion> {
        return buildList {
            setting.options.forEach { option ->
                when (option) {
                    is ReplySetting.CombineOption.Mentioned -> {
                        add(ThreadgateAllowUnion.MentionRule(ThreadgateMentionRule))
                    }

                    is ReplySetting.CombineOption.Following -> {
                        add(ThreadgateAllowUnion.FollowingRule(ThreadgateFollowingRule))
                    }

                    is ReplySetting.CombineOption.Followers -> {
                        add(ThreadgateAllowUnion.FollowerRule(ThreadgateFollowerRule))
                    }

                    is ReplySetting.CombineOption.UserInList -> {
                        add(ThreadgateAllowUnion.ListRule(ThreadgateListRule(AtUri(option.listView.uri))))
                    }
                }
            }
        }
    }

    private fun buildFacet(
        content: String,
        mentionedUsers: Set<ProfileView>,
    ): List<Facet> {
        if (content.isEmpty()) return emptyList()
        val facetList = mutableListOf<Facet>()
        val hashtags = HashtagTextUtils.findHashtags(content)
        for (hashtag in hashtags) {
            // bsky doesn't handle empty hashtags elegantly
            if (hashtag.start + 1 >= hashtag.end) continue
            val tag = content.substring(hashtag.start + 1, hashtag.end)
            val facet = Facet(
                index = convertIndex(hashtag, content),
                features = listOf(
                    FacetFeatureUnion.Tag(
                        value = FacetTag(tag = tag),
                    )
                ),
            )
            facetList += facet
        }
        val userRanges = MentionTextUtil.findMentionList(content)
            .filter { range -> hashtags.firstOrNull { it.intersects(range) } == null }
        for (userRange in userRanges) {
            val handle = content.substring(userRange.start, userRange.end)
            mentionedUsers.firstOrNull { it.handle.handle == handle.removePrefix("@") }
                ?.did
                ?.let { did ->
                    val facet = Facet(
                        index = convertIndex(userRange, content),
                        features = listOf(
                            FacetFeatureUnion.Mention(
                                value = FacetMention(did = did),
                            )
                        ),
                    )
                    facetList += facet
                }
        }
        val previousRanges = hashtags + userRanges
        val linkRanges = LinkTextUtils.findLinks(content)
            .filter { range ->
                previousRanges.firstOrNull { it.intersects(range) } == null
            }
        for (linkRange in linkRanges) {
            val link = content.substring(linkRange.start, linkRange.end)
            val facetLink = if (link.lowercase().startsWith("http")) {
                FacetLink(Uri(link))
            } else {
                FacetLink(Uri("http://$link"))
            }
            val facet = Facet(
                index = convertIndex(linkRange, content),
                features = listOf(
                    FacetFeatureUnion.Link(facetLink)
                ),
            )
            facetList += facet
        }
        return facetList
    }

    private fun convertIndex(range: TextRange, content: String): FacetByteSlice {
        return FacetByteSlice(
            byteStart = calculateUtf8Index(range.start, content),
            byteEnd = calculateUtf8Index(range.end, content),
        )
    }

    private fun calculateUtf8Index(
        index: Int,
        text: String,
    ): Long {
        return text.utf8Size(0, index.coerceAtMost(text.length))
    }
}
