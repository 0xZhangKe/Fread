package com.zhangke.fread.bluesky

import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.SearchActorsQueryParams
import app.bsky.feed.SearchPostsQueryParams
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.usecase.GetAtIdentifierUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot
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
) : ISearchEngine {

    override suspend fun search(
        role: IdentityRole,
        query: String,
    ): Result<List<SearchResult>> {
        return supervisorScope {
            val postsDeferred = async { searchStatus(role, query, null) }
            val actorsDeferred = async { searchAuthor(role, query, null) }
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
        role: IdentityRole,
        query: String,
        maxId: String?,
    ): Result<List<StatusUiState>> {
        val client = clientManager.getClient(role)
        val account = client.loggedAccountProvider()
        val platform = platformRepo.getPlatform(client.baseUrl)
        return client.searchPostsCatching(SearchPostsQueryParams(q = query))
            .map { result ->
                result.posts.map {
                    statusAdapter.convertToUiState(
                        role = role,
                        postView = it,
                        platform = platform,
                        loggedAccount = account,
                    )
                }
            }
    }

    override suspend fun searchHashtag(
        role: IdentityRole,
        query: String,
        offset: Int?,
    ): Result<List<Hashtag>> {
        return Result.success(emptyList())
    }

    override suspend fun searchAuthor(
        role: IdentityRole,
        query: String,
        offset: Int?,
    ): Result<List<BlogAuthor>> {
        val client = clientManager.getClient(role)
        return client.searchActorsCatching(SearchActorsQueryParams(q = query))
            .map { result ->
                result.actors.map { accountAdapter.convertToBlogAuthor(it) }
            }
    }

    override fun searchAuthablePlatform(query: String): Flow<List<PlatformSnapshot>>? {
        return null
    }

    override suspend fun searchSource(
        role: IdentityRole,
        query: String,
    ): Result<List<StatusSource>> {
        val client = clientManager.getClient(role)
        val identifier = getAtIdentifier(query) ?: return Result.success(emptyList())
        return client.getProfileCatching(GetProfileQueryParams(identifier))
            .map { profile -> listOf(accountAdapter.createSource(profile)) }
    }

    override fun searchContent(
        role: IdentityRole,
        query: String,
    ): Flow<List<SearchContentResult>> {
        return flow {
            blueskyPlatformRepo.getAllPlatform()
                .filter { it.compareWithQuery(query) }
                .map { it.toContentResult() }
                .let { emit(it) }
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
