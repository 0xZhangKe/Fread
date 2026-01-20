package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.feed.GetPostThreadQueryParams
import app.bsky.feed.GetPostThreadResponseThreadUnion
import app.bsky.feed.ThreadViewPostParentUnion
import app.bsky.feed.ThreadViewPostReplieUnion
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.DescendantStatus
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import sh.christian.ozone.api.AtUri

class GetStatusContextUseCase(
    private val clientManager: BlueskyClientManager,
    private val blogPlatformRepo: BlueskyPlatformRepo,
    private val statusAdapter: BlueskyStatusAdapter,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        status: Status,
    ): Result<StatusContext> {
        val client = clientManager.getClient(locator)
        val threadResult = client.getPostThreadCatching(
            GetPostThreadQueryParams(
                uri = AtUri(status.intrinsicBlog.url),
                depth = 6,
            )
        )
        if (threadResult.isFailure) return Result.failure(threadResult.exceptionOrNull()!!)
        val loggedAccount = client.loggedAccountProvider()
        val platform = blogPlatformRepo.getPlatform(client.baseUrl)
        when (val thread = threadResult.getOrThrow().thread) {
            is GetPostThreadResponseThreadUnion.Unknown,
            is GetPostThreadResponseThreadUnion.BlockedPost,
            is GetPostThreadResponseThreadUnion.NotFoundPost -> {
                return Result.failure(RuntimeException("Post not found"))
            }

            is GetPostThreadResponseThreadUnion.ThreadViewPost -> {
                val threadViewPost = thread.value
                val ancestors = buildAncestors(
                    locator = locator,
                    parent = threadViewPost.parent,
                    platform = platform,
                    loggedAccount = loggedAccount,
                )
                val descendants = thread.value.replies.mapNotNull { reply ->
                    if (reply is ThreadViewPostReplieUnion.ThreadViewPost) {
                        val statusUiState = statusAdapter.convertToUiState(
                            locator = locator,
                            postView = reply.value.post,
                            platform = platform,
                            loggedAccount = loggedAccount,
                        )
                        DescendantStatus(statusUiState, null)
                    } else {
                        null
                    }
                }
                return Result.success(
                    StatusContext(
                        ancestors = ancestors,
                        status = statusAdapter.convertToUiState(
                            locator = locator,
                            postView = threadViewPost.post,
                            platform = platform,
                            loggedAccount = loggedAccount,
                        ),
                        descendants = descendants,
                    )
                )
            }
        }
    }

    private fun buildAncestors(
        locator: PlatformLocator,
        parent: ThreadViewPostParentUnion?,
        platform: BlogPlatform,
        loggedAccount: BlueskyLoggedAccount?,
    ): List<StatusUiState> {
        if (parent == null) return emptyList()
        val ancestors = mutableListOf<StatusUiState>()
        var currentParent: ThreadViewPostParentUnion? = parent
        while (currentParent != null) {
            if (currentParent !is ThreadViewPostParentUnion.ThreadViewPost) break
            val statusUiState = statusAdapter.convertToUiState(
                locator = locator,
                postView = currentParent.value.post,
                platform = platform,
                loggedAccount = loggedAccount,
            )
            ancestors.add(0, statusUiState)
            currentParent = currentParent.value.parent
        }
        return ancestors
    }
}
