package com.zhangke.fread.activitypub.app

import com.zhangke.activitypub.api.SearchRepo
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubSearchAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.usecase.GetDefaultBaseUrlUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.source.user.SearchUserSourceUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.search.SearchResult
import com.zhangke.fread.status.source.StatusSource
import me.tatarka.inject.annotations.Inject

class ActivityPubSearchEngine @Inject constructor(
    private val searchUserSource: SearchUserSourceUseCase,
    private val clientManager: ActivityPubClientManager,
    private val platformRepo: ActivityPubPlatformRepo,
    private val searchAdapter: ActivityPubSearchAdapter,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val hashtagAdapter: ActivityPubTagAdapter,
    private val accountAdapter: ActivityPubAccountEntityAdapter,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val getDefaultBaseUrl: GetDefaultBaseUrlUseCase,
) : ISearchEngine {

    override suspend fun search(
        locator: PlatformLocator,
        query: String
    ): Result<List<SearchResult>> {
        val account = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
        return doSearch(locator) { searchRepo, platform ->
            searchRepo.query(query).map {
                searchAdapter.toSearchResult(it, platform, locator, account)
            }
        }
    }

    override suspend fun searchStatus(
        locator: PlatformLocator,
        query: String,
        maxId: String?,
    ): Result<List<StatusUiState>> {
        val account = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
        return doSearch(locator) { searchRepo, blogPlatform ->
            searchRepo.queryStatus(
                query = query,
                maxId = maxId,
            ).map { list ->
                list.map {
                    statusAdapter.toStatusUiState(
                        entity = it,
                        platform = blogPlatform,
                        locator = locator,
                        loggedAccount = account,
                    )
                }
            }
        }
    }

    override suspend fun searchHashtag(
        locator: PlatformLocator,
        query: String,
        offset: Int?,
    ): Result<List<Hashtag>> {
        return doSearch(locator) { searchRepo, _ ->
            searchRepo.queryHashtags(
                query = query,
                offset = offset,
            ).map { list ->
                list.map { hashtagAdapter.adapt(it) }
            }
        }
    }

    override suspend fun searchAuthor(
        locator: PlatformLocator,
        query: String,
        offset: Int?,
    ): Result<List<BlogAuthor>> {
        return doSearch(locator) { searchRepo, _ ->
            searchRepo.queryAccount(
                query = query,
                offset = offset,
            ).map { list ->
                list.map { accountAdapter.toAuthor(it) }
            }
        }
    }

    private suspend fun <T> doSearch(
        locator: PlatformLocator,
        onSearch: suspend (SearchRepo, BlogPlatform) -> Result<List<T>>,
    ): Result<List<T>> {
        val platformResult = platformRepo.getPlatform(locator)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val searchRepo = clientManager.getClient(locator).searchRepo
        return onSearch(searchRepo, platform)
    }

    override suspend fun searchSourceNoToken(query: String): Result<List<StatusSource>> {
        return searchUserSource(PlatformLocator(baseUrl = getDefaultBaseUrl()), query)
    }
}
