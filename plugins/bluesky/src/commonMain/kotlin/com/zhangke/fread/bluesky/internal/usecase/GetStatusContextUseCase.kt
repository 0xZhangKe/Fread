package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.feed.GetPostThreadQueryParams
import app.bsky.feed.GetPostThreadResponseThreadUnion
import app.bsky.feed.ThreadViewPostParentUnion
import app.bsky.feed.ThreadViewPostReplieUnion
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri

class GetStatusContextUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val buildBskyStatus: BuildBskyStatusUseCase,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        status: Status,
    ): Result<StatusContext> {
        val client = clientManager.getClient(role)
        val threadResult = client.getPostThreadCatching(
            GetPostThreadQueryParams(
                uri = AtUri(status.intrinsicBlog.url),
                depth = 6,
            )
        )
        if (threadResult.isFailure) return Result.failure(threadResult.exceptionOrNull()!!)
        when (val thread = threadResult.getOrThrow().thread) {
            is GetPostThreadResponseThreadUnion.BlockedPost,
            is GetPostThreadResponseThreadUnion.NotFoundPost -> {
                return Result.failure(RuntimeException("Post not found"))
            }

            is GetPostThreadResponseThreadUnion.ThreadViewPost -> {
                val threadViewPost = thread.value
                val ancestors = buildAncestors(client.baseUrl, threadViewPost.parent)
                val descendants = thread.value.replies.mapNotNull { reply ->
                    if (reply is ThreadViewPostReplieUnion.ThreadViewPost) {
                        buildBskyStatus(client.baseUrl, reply.value.post)
                    } else {
                        null
                    }
                }
                return Result.success(
                    StatusContext(
                        ancestors = ancestors,
                        status = buildBskyStatus(client.baseUrl, threadViewPost.post),
                        descendants = descendants,
                    )
                )
            }
        }
    }

    private suspend fun buildAncestors(
        baseUrl: FormalBaseUrl,
        parent: ThreadViewPostParentUnion?,
    ): List<Status> {
        if (parent == null) return emptyList()
        val ancestors = mutableListOf<Status>()
        var currentParent: ThreadViewPostParentUnion? = parent
        while (currentParent != null) {
            if (currentParent !is ThreadViewPostParentUnion.ThreadViewPost) break
            val status = buildBskyStatus(baseUrl, currentParent.value.post)
            ancestors.add(0, status)
            currentParent = currentParent.value.parent
        }
        return ancestors
    }
}
