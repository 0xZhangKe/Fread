package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.feed.FeedViewPost
import app.bsky.feed.FeedViewPostReasonUnion
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.model.ProcessingBskyPost
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import me.tatarka.inject.annotations.Inject

class BuildBskyStatusUseCase @Inject constructor(
    private val accountManager: BlueskyLoggedAccountManager,
    private val platformRepo: BlueskyPlatformRepo,
    private val statusAdapter: BlueskyStatusAdapter,
) {

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        feedViewPost: FeedViewPost,
    ): Status {
        val platform = platformRepo.getPlatform(baseUrl)
        val allCount = accountManager.getAllAccount()
        val self = accountManager.getAllAccount().any { it.did == feedViewPost.post.author.did.did }
        val processingPost = feedViewPost.toProcessingPost()
        return statusAdapter.convert(
            processingBskyPost = processingPost,
            platform = platform,
            isSelfStatus = self,
            supportInteraction = buildInteractions(
                processingPost = processingPost,
                logged = allCount.isNotEmpty(),
                self = self,
            ),
        )
    }

    private fun buildInteractions(
        processingPost: ProcessingBskyPost,
        logged: Boolean,
        self: Boolean,
    ): List<StatusInteraction> {
        val post = processingPost.postView
        val actionList = mutableListOf<StatusInteraction>()
        actionList += StatusInteraction.Like(
            likeCount = post.likeCount.toIntOrNull(),
            liked = post.viewer?.like?.atUri.isNullOrEmpty().not(),
            enable = logged,
        )
        actionList += StatusInteraction.Forward(
            forwardCount = post.repostCount.toIntOrNull(),
            forwarded = post.viewer?.repost?.atUri.isNullOrEmpty().not(),
            enable = logged,
        )
        actionList += StatusInteraction.Comment(
            commentCount = post.replyCount.toIntOrNull(),
            enable = logged,
        )
        actionList += StatusInteraction.Bookmark(
            bookmarkCount = null,
            bookmarked = false,
            enable = false,
        )
        if (self) {
            actionList.add(StatusInteraction.Delete(enable = true))
            val pinned = processingPost.pinned
            actionList.add(StatusInteraction.Pin(pinned = pinned, enable = true))
            // just edit interaction limit
            actionList.add(StatusInteraction.Edit(true))
        }
        return actionList
    }

    private fun Long?.toIntOrNull(): Int {
        return this?.toInt() ?: 0
    }

    private fun FeedViewPost.toProcessingPost(): ProcessingBskyPost {
        return ProcessingBskyPost(
            post = bskyJson.decodeFromJsonElement(bskyJson.encodeToJsonElement(this.post.record)),
            postView = this.post,
            reason = reason,
            pinned = reason is FeedViewPostReasonUnion.ReasonPin,
        )
    }
}
