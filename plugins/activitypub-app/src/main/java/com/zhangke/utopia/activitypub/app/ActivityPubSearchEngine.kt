package com.zhangke.utopia.activitypub.app

import com.zhangke.activitypub.api.SearchRepo
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubSearchAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.source.user.SearchUserSourceUseCase
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.PlatformSnapshot
import com.zhangke.utopia.status.search.ISearchEngine
import com.zhangke.utopia.status.search.SearchContentResult
import com.zhangke.utopia.status.search.SearchResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class ActivityPubSearchEngine @Inject constructor(
    private val searchUserSource: SearchUserSourceUseCase,
    private val clientManager: ActivityPubClientManager,
    private val platformRepo: ActivityPubPlatformRepo,
    private val searchAdapter: ActivityPubSearchAdapter,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val hashtagAdapter: ActivityPubTagAdapter,
    private val accountAdapter: ActivityPubAccountEntityAdapter,
) : ISearchEngine {

    override suspend fun search(role: IdentityRole, query: String): Result<List<SearchResult>> {
        return doSearch(role) { searchRepo, platform ->
            searchRepo.query(query).map {
                searchAdapter.toSearchResult(it, platform)
            }
        }
    }

    override suspend fun searchStatus(
        role: IdentityRole,
        query: String,
        maxId: String?,
    ): Result<List<Status>> {
        return doSearch(role) { searchRepo, blogPlatform ->
            searchRepo.queryStatus(
                query = query,
                maxId = maxId,
            ).map { list ->
                list.map { statusAdapter.toStatus(it, blogPlatform) }
            }
        }
    }

    override suspend fun searchHashtag(
        role: IdentityRole,
        query: String,
        offset: Int?,
    ): Result<List<Hashtag>> {
        return doSearch(role) { searchRepo, _ ->
            searchRepo.queryHashtags(
                query = query,
                offset = offset,
            ).map { list ->
                list.map { hashtagAdapter.adapt(it) }
            }
        }
    }

    override suspend fun searchAuthor(
        role: IdentityRole,
        query: String,
        offset: Int?,
    ): Result<List<BlogAuthor>> {
        return doSearch(role) { searchRepo, _ ->
            searchRepo.queryAccount(
                query = query,
                offset = offset,
            ).map { list ->
                list.map { accountAdapter.toAuthor(it) }
            }
        }
    }

    override suspend fun searchPlatform(
        query: String,
        offset: Int?,
    ): Result<List<BlogPlatform>> {
        if (offset != null && offset > 0) {
            return Result.success(emptyList())
        }
        val baseUrl = FormalBaseUrl.parse(query) ?: return Result.success(emptyList())
        return platformRepo.getPlatform(baseUrl).map { listOf(it) }
    }

    override suspend fun searchPlatformSnapshot(query: String): List<PlatformSnapshot> {
        return platformRepo.searchPlatformSnapshot(query)
    }

    private suspend fun <T> doSearch(
        role: IdentityRole,
        onSearch: suspend (SearchRepo, BlogPlatform) -> Result<List<T>>,
    ): Result<List<T>> {
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val searchRepo = clientManager.getClient(role).searchRepo
        return onSearch(searchRepo, platform)
    }

    override suspend fun searchSource(
        role: IdentityRole,
        query: String,
    ): Result<List<StatusSource>> {
        return searchUserSource(role, query).map {
            if (it == null) {
                emptyList()
            } else {
                listOf(it)
            }
        }
    }

    override suspend fun searchContent(
        role: IdentityRole,
        query: String,
    ): List<SearchContentResult> {
        val searchResultList = mutableListOf<SearchContentResult>()
        searchUserSource(role, query).getOrNull()
            ?.let { searchResultList += SearchContentResult.Source(it) }
        platformRepo.searchPlatformSnapshot(query)
            .takeIf { it.isNotEmpty() }
            ?.map {
                searchResultList += SearchContentResult.ActivityPubPlatformSnapshot(it)
            }
        FormalBaseUrl.parse(query)
            ?.let { platformRepo.getPlatform(it).getOrNull() }
            ?.let { searchResultList += SearchContentResult.ActivityPubPlatform(it) }
        return searchResultList
    }
}
