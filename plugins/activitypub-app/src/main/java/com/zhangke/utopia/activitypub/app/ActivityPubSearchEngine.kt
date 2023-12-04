package com.zhangke.utopia.activitypub.app

import com.zhangke.framework.utils.collect
import com.zhangke.utopia.activitypub.app.internal.usecase.source.timeline.SearchTimelineSourceUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.source.user.SearchUserSourceUseCase
import com.zhangke.utopia.status.search.IUtopiaSearchEngine
import com.zhangke.utopia.status.search.SearchResult
import javax.inject.Inject

class ActivityPubSearchEngine @Inject constructor(
    private val searchUserSource: SearchUserSourceUseCase,
    private val searchTimelineSource: SearchTimelineSourceUseCase,
) : IUtopiaSearchEngine {

    override suspend fun search(query: String): Result<List<SearchResult>> {
        return listOf(
            searchUserSource(query),
            searchTimelineSource(query),
        ).collect().map { list ->
            list.map { SearchResult.Source(it) }
        }
    }
}
