package com.zhangke.fread.bluesky

import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.SearchActorsQueryParams
import app.bsky.feed.SearchPostsQueryParams
import app.bsky.feed.SearchPostsSort
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.usecase.GetAtIdentifierUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.search.SearchContentResult
import com.zhangke.fread.status.search.SearchResult
import com.zhangke.fread.status.search.SearchStatusSort
import com.zhangke.fread.status.source.StatusSource
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

class BlueskySearchEngine(
    private val clientManager: BlueskyClientManager,
    private val accountAdapter: BlueskyAccountAdapter,
    private val getAtIdentifier: GetAtIdentifierUseCase,
    private val blueskyPlatformRepo: BlueskyPlatformRepo,
    private val statusAdapter: BlueskyStatusAdapter,
    private val platformRepo: BlueskyPlatformRepo,
    private val accountManager: BlueskyLoggedAccountManager,
) : ISearchEngine {

    // Bluesky paginates by an opaque cursor, not by a numeric offset or
    // post-id. The ISearchEngine interface only carries `maxId`/`offset`, so we
    // keep the cursor here, keyed by query (and sort, for statuses). A fresh
    // call (`maxId`/`offset` null), a query change, or a sort change resets
    // the cursor; subsequent calls reuse it. Engine is bound as a singleton
    // in BlueskyModule so this state survives across calls in a search
    // session.
    private var statusQuery: String? = null
    private var statusSort: SearchStatusSort? = null
    private var statusCursor: String? = null
    private var authorQuery: String? = null
    private var authorCursor: String? = null

    override suspend fun search(
        locator: PlatformLocator,
        query: String,
    ): Result<List<SearchResult>> {
        return supervisorScope {
            val postsDeferred = async { searchStatus(locator, query, null) }
            val actorsDeferred = async { searchAuthor(locator, query, null) }
            val postsResult = postsDeferred.await()
            val actorResult = actorsDeferred.await()
            if (postsResult.isFailure && actorResult.isFailure) {
                Result.failure(
                    postsResult.exceptionOrNull() ?: actorResult.exceptionOrNull()!!
                )
            } else {
                val status: List<SearchResult> = postsResult.getOrNull()
                    ?.map { SearchResult.SearchedStatus(it) } ?: emptyList()
                val actors: List<SearchResult> = actorResult.getOrNull()
                    ?.map { actor -> SearchResult.Author(actor) } ?: emptyList()
                Result.success(actors + status)
            }
        }
    }

    override suspend fun searchStatus(
        locator: PlatformLocator,
        query: String,
        maxId: String?,
        sort: SearchStatusSort,
    ): Result<List<StatusUiState>> {
        val freshSearch = maxId == null || query != statusQuery || sort != statusSort
        if (freshSearch) {
            statusQuery = query
            statusSort = sort
            statusCursor = null
        } else if (statusCursor.isNullOrBlank()) {
            return Result.success(emptyList())
        }
        val client = clientManager.getClient(locator)
        val account = client.loggedAccountProvider()
        val platform = platformRepo.getPlatform(client.baseUrl)
        return client.searchPostsCatching(
            SearchPostsQueryParams(
                q = query,
                cursor = statusCursor,
                sort = sort.toBluesky(),
            )
        )
            .onSuccess { statusCursor = it.cursor }
            .map { result ->
                result.posts.map {
                    statusAdapter.convertToUiState(
                        locator = locator,
                        postView = it,
                        platform = platform,
                        loggedAccount = account,
                    )
                }
            }
    }

    override suspend fun searchHashtag(
        locator: PlatformLocator,
        query: String,
        offset: Int?,
    ): Result<List<Hashtag>> {
        return Result.success(emptyList())
    }

    override suspend fun searchAuthor(
        locator: PlatformLocator,
        query: String,
        offset: Int?,
    ): Result<List<BlogAuthor>> {
        val freshSearch = offset == null || offset == 0 || query != authorQuery
        if (freshSearch) {
            authorQuery = query
            authorCursor = null
        } else if (authorCursor.isNullOrBlank()) {
            return Result.success(emptyList())
        }
        val client = clientManager.getClient(locator)
        return client.searchActorsCatching(SearchActorsQueryParams(q = query, cursor = authorCursor))
            .onSuccess { authorCursor = it.cursor }
            .map { result ->
                result.actors.map { accountAdapter.convertToBlogAuthor(it) }
            }
    }

    private fun SearchStatusSort.toBluesky(): SearchPostsSort = when (this) {
        SearchStatusSort.LATEST -> SearchPostsSort.Latest
        SearchStatusSort.TOP -> SearchPostsSort.Top
    }

    override suspend fun searchSourceNoToken(query: String): Result<List<StatusSource>> {
        val client = clientManager.getClient(getDefaultPlatformLocator())
        val identifier = getAtIdentifier(query)
            ?: return client.searchActorsCatching(SearchActorsQueryParams(q = query, limit = 10))
                .map { result ->
                    result.actors.map { accountAdapter.createSource(it) }
                }
        return client.getProfileCatching(GetProfileQueryParams(identifier))
            .map { profile -> listOf(accountAdapter.createSource(profile)) }
    }

    private suspend fun getDefaultPlatformLocator(): PlatformLocator {
        val baseUrl = accountManager.getAllAccount().firstOrNull()?.platform?.baseUrl
            ?: blueskyPlatformRepo.getAllPlatform().first().baseUrl
        return PlatformLocator(baseUrl = baseUrl)
    }

    private fun BlogPlatform.compareWithQuery(query: String): Boolean {
        if (this.name.contains(query)) return true
        if (this.baseUrl.toString().contains(query)) return true
        return uri.contains(query)
    }

    private fun BlogPlatform.toContentResult(): SearchContentResult {
        return SearchContentResult.Platform(this)
    }
}
