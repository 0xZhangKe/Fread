package com.zhangke.utopia.activitypub.app

import com.zhangke.activitypub.api.SearchRepo
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubSearchAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.search.SearchPlatformUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.source.user.SearchUserSourceUseCase
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.search.ISearchEngine
import com.zhangke.utopia.status.search.SearchContentResult
import com.zhangke.utopia.status.search.SearchResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class ActivityPubSearchEngine @Inject constructor(
    private val searchUserSource: SearchUserSourceUseCase,
    private val baseUrlManager: BaseUrlManager,
    private val clientManager: ActivityPubClientManager,
    private val platformRepo: ActivityPubPlatformRepo,
    private val searchAdapter: ActivityPubSearchAdapter,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val hashtagAdapter: ActivityPubTagAdapter,
    private val accountAdapter: ActivityPubAccountEntityAdapter,
    private val searchPlatform: SearchPlatformUseCase,
) : ISearchEngine {

    override suspend fun search(query: String): Result<List<SearchResult>> {
        return doSearch { searchRepo, platform ->
            searchRepo.query(query).map {
                searchAdapter.toSearchResult(it, platform)
            }
        }
    }

    override suspend fun searchStatus(
        query: String,
        maxId: String?,
    ): Result<List<Status>> {
        return doSearch { searchRepo, blogPlatform ->
            searchRepo.queryStatus(
                query = query,
                maxId = maxId,
            ).map { list ->
                list.map { statusAdapter.toStatus(it, blogPlatform) }
            }
        }
    }

    override suspend fun searchHashtag(
        query: String,
        offset: Int?,
    ): Result<List<Hashtag>> {
        return doSearch { searchRepo, _ ->
            searchRepo.queryHashtags(
                query = query,
                offset = offset,
            ).map { list ->
                list.map { hashtagAdapter.adapt(it) }
            }
        }
    }

    override suspend fun searchAuthor(
        query: String,
        offset: Int?,
    ): Result<List<BlogAuthor>> {
        return doSearch { searchRepo, _ ->
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
        return searchPlatform(query)
    }

    private suspend fun <T> doSearch(
        onSearch: suspend (SearchRepo, BlogPlatform) -> Result<List<T>>,
    ): Result<List<T>> {
        val baseUrl = baseUrlManager.decideBaseUrl()
        val platformResult = platformRepo.getPlatform(baseUrl)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val searchRepo = clientManager.getClient(baseUrl).searchRepo
        return onSearch(searchRepo, platform)
    }

    override suspend fun searchSource(query: String): Result<List<StatusSource>> {
        return searchUserSource(query).map {
            if (it == null) {
                emptyList()
            } else {
                listOf(it)
            }
        }
    }

    override suspend fun searchContent(query: String): Result<List<SearchContentResult>>? {
        val searchedPlatform = searchPlatform(query).getOrNull()
        if (searchedPlatform.isNullOrEmpty().not()){
            return Result.success(searchedPlatform!!.map { SearchContentResult.ActivityPubPlatform(it) })
        }
        val searchedSource = searchUserSource(query).getOrNull()
        if (searchedSource != null) {
            return Result.success(listOf(SearchContentResult.Source(searchedSource)))
        }
        return null
    }
}
