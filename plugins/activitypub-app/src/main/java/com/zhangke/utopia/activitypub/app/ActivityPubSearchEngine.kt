package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubSearchAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.source.user.SearchUserSourceUseCase
import com.zhangke.utopia.status.search.ISearchEngine
import com.zhangke.utopia.status.search.SearchResult
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class ActivityPubSearchEngine @Inject constructor(
    private val searchUserSource: SearchUserSourceUseCase,
    private val baseUrlManager: BaseUrlManager,
    private val clientManager: ActivityPubClientManager,
    private val platformRepo: ActivityPubPlatformRepo,
    private val searchAdapter: ActivityPubSearchAdapter,
) : ISearchEngine {

    override suspend fun query(query: String): Result<List<SearchResult>> {
        val baseUrl = baseUrlManager.decideBaseUrl()
        val platformResult = platformRepo.getPlatform(baseUrl)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val searchRepo = clientManager.getClient(baseUrl).searchRepo
        return searchRepo.query(query).map {
            searchAdapter.toSearchResult(it, platform)
        }
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
}
