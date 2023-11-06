package com.zhangke.utopia.activitypub.app.search

import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.utils.collect
import com.zhangke.utopia.activitypub.app.internal.source.timeline.SearchTimelineSourceUseCase
import com.zhangke.utopia.activitypub.app.internal.source.user.SearchUserSourceUseCase
import com.zhangke.utopia.status.search.IUtopiaSearchEngine
import com.zhangke.utopia.status.search.SearchResult
import javax.inject.Inject

@Filt
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
