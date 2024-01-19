package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.usecase.source.user.SearchUserSourceUseCase
import com.zhangke.utopia.status.search.IUtopiaSearchEngine
import com.zhangke.utopia.status.search.SearchResult
import javax.inject.Inject

class ActivityPubSearchEngine @Inject constructor(
    private val searchUserSource: SearchUserSourceUseCase,
) : IUtopiaSearchEngine {

    override suspend fun search(query: String): Result<List<SearchResult>> {
        return searchUserSource(query).map {
            if (it == null) {
                emptyList()
            } else {
                listOf(SearchResult.Source(it))
            }
        }
    }
}
