package com.zhangke.utopia.activitypubapp.source

import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.utils.collect
import com.zhangke.utopia.activitypubapp.source.timeline.SearchTimelineSourceUseCase
import com.zhangke.utopia.activitypubapp.source.user.SearchUserSourceUseCase
import com.zhangke.utopia.status.search.ISearchStatusSourceUseCase
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

@Filt
class SearchActivityPubStatusSourceUseCase @Inject constructor(
    private val searchUserSource: SearchUserSourceUseCase,
    private val searchTimelineSource: SearchTimelineSourceUseCase,
) : ISearchStatusSourceUseCase {

    override suspend fun invoke(query: String): Result<List<StatusSource>> {
        return listOf(
            searchUserSource(query),
            searchTimelineSource(query),
        ).collect()
    }
}
