package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion.SavedFeedsPrefV2
import app.bsky.actor.SavedFeed
import app.bsky.actor.Type
import app.bsky.feed.GeneratorView
import app.bsky.feed.GetFeedGeneratorsQueryParams
import app.bsky.graph.GetListQueryParams
import app.bsky.graph.ListView
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.adapter.BlueskyFeedsAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri

class GetFollowingFeedsUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val feedsAdapter: BlueskyFeedsAdapter,
) {

    suspend operator fun invoke(locator: PlatformLocator): Result<List<BlueskyFeeds>> {
        val client = clientManager.getClient(locator)
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
        val allListViewsResult = getListViews(client, followingFeeds)
        if (allListViewsResult.isFailure) return Result.failure(allListViewsResult.exceptionOrThrow())
        val allListViews = allListViewsResult.getOrThrow()
        return followingFeeds.mapNotNull { feed ->
            mapFollowingFeeds(feed, generatorList, allListViews)
        }.let { Result.success(it.distinctBy { it.id }) }
    }

    private fun mapFollowingFeeds(
        feed: SavedFeed,
        generatorList: List<GeneratorView>,
        listViewList: List<ListView>,
    ): BlueskyFeeds? = when (feed.type) {
        Type.Feed -> {
            val generator = generatorList.firstOrNull { it.uri.atUri == feed.value }
            if (generator == null) {
                null
            } else {
                feedsAdapter.convertToFeeds(feed, generator)
            }
        }

        Type.List -> {
            val listView = listViewList.firstOrNull { it.uri.atUri == feed.value }
            if (listView == null) {
                null
            } else {
                feedsAdapter.convertToList(feed, listView)
            }
        }

        Type.Timeline -> {
            BlueskyFeeds.FollowingTimeline(feed.pinned)
        }

        else -> null
    }

    private suspend fun getListViews(
        client: BlueskyClient,
        feeds: List<SavedFeed>,
    ): Result<List<ListView>> {
        val lists = feeds.filter { it.type == Type.List }.map { it.value }
        if (lists.isEmpty()) return Result.success(emptyList())
        val allResults = supervisorScope {
            lists.map { uri ->
                async { client.getListCatching(GetListQueryParams(AtUri(uri))) }
            }.awaitAll()
        }
        if (allResults.all { it.isFailure }) {
            return Result.failure(allResults.first().exceptionOrNull()!!)
        }
        return allResults.mapNotNull { it.getOrNull()?.list }.let { Result.success(it) }
    }

    private suspend fun getGeneratorList(
        client: BlueskyClient,
        feeds: List<SavedFeed>,
    ): Result<List<GeneratorView>> {
        val feedsUris = feeds.filter { it.type == Type.Feed }
            .map { AtUri(it.value) }
        if (feedsUris.isEmpty()) return Result.success(emptyList())
        return client.getFeedGeneratorsCatching(
            GetFeedGeneratorsQueryParams(feedsUris)
        ).map { it.feeds }
    }
}
