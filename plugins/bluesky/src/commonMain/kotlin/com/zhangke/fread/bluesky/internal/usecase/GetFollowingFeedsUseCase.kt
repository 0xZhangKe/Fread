package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion.SavedFeedsPrefV2
import app.bsky.actor.SavedFeed
import app.bsky.actor.Type
import app.bsky.feed.GeneratorView
import app.bsky.feed.GetFeedGeneratorsQueryParams
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.adapter.BlueskyFeedsAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.collections.immutable.toImmutableList
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri

class GetFollowingFeedsUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val feedsAdapter: BlueskyFeedsAdapter,
) {

    suspend operator fun invoke(role: IdentityRole): Result<List<BlueskyFeeds>> {
        val client = clientManager.getClient(role)
        val preferenceResult = client.getPreferencesCatching()
        if (preferenceResult.isFailure) return Result.failure(preferenceResult.exceptionOrThrow())
        val preference = preferenceResult.getOrThrow()
        val followingFeeds = preference.preferences.filterIsInstance<SavedFeedsPrefV2>()
            .firstOrNull()
            ?.value
            ?.items
        if (followingFeeds.isNullOrEmpty()) return Result.success(emptyList())
        val generatorListResult = getGeneratorList(client, followingFeeds)
        if (generatorListResult.isFailure) return Result.failure(generatorListResult.exceptionOrThrow())
        val generatorList = generatorListResult.getOrThrow()
        return followingFeeds.mapNotNull { feed ->
            if (feed.type == Type.FEED) {
                val generator = generatorList.firstOrNull { it.uri.atUri == feed.value }
                if (generator == null) {
                    null
                } else {
                    feedsAdapter.convert(feed, generator)
                }
            } else if (feed.type == Type.LIST) {
                feedsAdapter.convertToList(feed, true)
            } else {
                BlueskyFeeds.Following(feed.pinned, true)
            }
        }.let { Result.success(it) }
    }

    private suspend fun getGeneratorList(
        client: BlueskyClient,
        feeds: List<SavedFeed>,
    ): Result<List<GeneratorView>> {
        val feedsUris = feeds.filter { it.type == Type.FEED }
            .map { AtUri(it.value) }
            .toImmutableList()
        return client.getFeedGeneratorsCatching(
            GetFeedGeneratorsQueryParams(feedsUris)
        ).map { it.feeds }
    }
}
