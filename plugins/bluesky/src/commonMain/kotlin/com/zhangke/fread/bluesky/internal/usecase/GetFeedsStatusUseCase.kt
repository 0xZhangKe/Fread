package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.feed.GetListFeedQueryParams
import app.bsky.feed.Post
import app.bsky.graph.GetListsQueryParams
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri

class GetFeedsStatusUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        feeds: BlueskyFeeds,
        cursor: String? = null,
    ): Result<List<Status>> {

        return Result.success(emptyList())
    }

    private suspend fun getListStatus(
        client: BlueskyClient,
        feeds: BlueskyFeeds.List,
        cursor: String?,
    ): Result<List<Status>> {
        client.getListFeedCatching(GetListFeedQueryParams(list = AtUri(feeds.uri), cursor = cursor))
        Post
        return Result.success(emptyList())
    }
}
