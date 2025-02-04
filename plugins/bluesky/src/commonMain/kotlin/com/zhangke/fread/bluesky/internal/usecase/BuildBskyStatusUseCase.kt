package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.feed.FeedViewPost
import app.bsky.feed.FeedViewPostReasonUnion
import app.bsky.feed.PostView
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.model.ProcessingBskyPost
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
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
        val self = accountManager.getAllAccount().any { it.did == feedViewPost.post.author.did.did }
        val processingPost = feedViewPost.toProcessingPost()
        return statusAdapter.convert(
            processingBskyPost = processingPost,
            platform = platform,
            isSelfStatus = self,
            supportInteraction = buildInteractions(
                post = processingPost.postView,
                pinned = processingPost.pinned,
                self = self,
            ),
        )
    }

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        postView: PostView,
    ): Status {
        val platform = platformRepo.getPlatform(baseUrl)
        val self = accountManager.getAllAccount().any { it.did == postView.author.did.did }
        return statusAdapter.convert(
            postView = postView,
            platform = platform,
            isSelfStatus = self,
            supportInteraction = buildInteractions(
                post = postView,
                pinned = false,
                self = self,
            ),
        )
    }

    private fun buildInteractions(
        post: PostView,
        pinned: Boolean,
        self: Boolean,
    ): List<StatusInteraction> {
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
            postView = this.post,
            reason = reason,
            pinned = reason is FeedViewPostReasonUnion.ReasonPin,
        )
    }
}
