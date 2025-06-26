package com.zhangke.fread.bluesky

import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.SearchActorsQueryParams
import app.bsky.feed.SearchPostsQueryParams
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
import com.zhangke.fread.status.source.StatusSource
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import me.tatarka.inject.annotations.Inject

class BlueskySearchEngine @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val accountAdapter: BlueskyAccountAdapter,
    private val getAtIdentifier: GetAtIdentifierUseCase,
    private val blueskyPlatformRepo: BlueskyPlatformRepo,
    private val statusAdapter: BlueskyStatusAdapter,
    private val platformRepo: BlueskyPlatformRepo,
    private val accountManager: BlueskyLoggedAccountManager,
) : ISearchEngine {

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
    ): Result<List<StatusUiState>> {
        val client = clientManager.getClient(locator)
        val account = client.loggedAccountProvider()
        val platform = platformRepo.getPlatform(client.baseUrl)
        return client.searchPostsCatching(SearchPostsQueryParams(q = query))
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
        val client = clientManager.getClient(locator)
        return client.searchActorsCatching(SearchActorsQueryParams(q = query))
            .map { result ->
                result.actors.map { accountAdapter.convertToBlogAuthor(it) }
            }
    }

    override suspend fun searchSourceNoToken(query: String): Result<List<StatusSource>> {
        return searchSource(getDefaultPlatformLocator(), query)
    }

    override suspend fun searchSource(
        locator: PlatformLocator,
        query: String,
    ): Result<List<StatusSource>> {
        val client = clientManager.getClient(locator)
        val identifier = getAtIdentifier(query)
            ?: return client.searchActorsCatching(SearchActorsQueryParams(q = query))
                .map { result ->
                    result.actors.map { accountAdapter.createSource(it) }
                }
        return client.getProfileCatching(GetProfileQueryParams(identifier))
            .map { profile -> listOf(accountAdapter.createSource(profile)) }
    }

    override suspend fun searchContentNoToken(query: String): Flow<List<SearchContentResult>> {
        val locator = getDefaultPlatformLocator()
        return searchContent(locator, query)
    }

    private suspend fun getDefaultPlatformLocator(): PlatformLocator {
        val baseUrl = accountManager.getAllAccount().firstOrNull()?.platform?.baseUrl
            ?: blueskyPlatformRepo.getAllPlatform().first().baseUrl
        return PlatformLocator(baseUrl = baseUrl)
    }

    override suspend fun searchContent(
        locator: PlatformLocator,
        query: String,
    ): Flow<List<SearchContentResult>> {
        return flow {
            blueskyPlatformRepo.getAllPlatform()
                .filter { it.compareWithQuery(query) }
                .map { it.toContentResult() }
                .let { emit(it) }
            searchSource(locator, query = query)
                .onSuccess { list ->
                    emit(list.map { SearchContentResult.Source(it) })
                }
            clientManager.getClient(locator)
                .searchActorsCatching(SearchActorsQueryParams(q = query))
                .map { result ->
                    result.actors.map { accountAdapter.createSource(it) }
                }.onSuccess { list ->
                    emit(list.map { SearchContentResult.Source(it) })
                }
        }
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
