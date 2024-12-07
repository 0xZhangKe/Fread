package com.zhangke.fread.activitypub.app

import com.zhangke.activitypub.api.SearchRepo
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.Log
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubSearchAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.usecase.source.user.SearchUserSourceUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.search.SearchContentResult
import com.zhangke.fread.status.search.SearchResult
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.status.model.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject

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

    override fun searchAuthablePlatform(query: String): Flow<List<PlatformSnapshot>>? {
        return platformRepo.searchAuthablePlatform(query)
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

    override fun searchContent(
        role: IdentityRole,
        query: String,
    ): Flow<List<SearchContentResult>> {
        return flow {
            searchUserSource(role, query).getOrNull()
                ?.let { emit(listOf(SearchContentResult.Source(it))) }
            platformRepo.searchPlatformSnapshotFromLocal(query)
                .map { SearchContentResult.SearchedPlatformSnapshot(it) }
                .takeIf { it.isNotEmpty() }
                ?.let { emit(it) }
            FormalBaseUrl.parse(query)
                ?.let { platformRepo.getPlatform(it).getOrNull() }
                ?.let { emit(listOf(SearchContentResult.ActivityPubPlatform(it))) }
            platformRepo.searchPlatformFromServer(query)
                .also {
                    Log.i("F_TEST") { it.toString() }
                }
                .getOrNull()
                ?.map { SearchContentResult.SearchedPlatformSnapshot(it) }
                ?.takeIf { it.isNotEmpty() }
                ?.let { emit(it) }
        }
    }
}
