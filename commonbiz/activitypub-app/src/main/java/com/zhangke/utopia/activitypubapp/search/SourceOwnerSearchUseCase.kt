package com.zhangke.utopia.activitypubapp.search

import com.zhangke.utopia.activitypubapp.adapter.InstanceSearchAdapter
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.search.IStatusProviderSearchUseCase
import com.zhangke.utopia.status.search.StatusProviderSearchResult
import javax.inject.Inject

class SourceOwnerSearchUseCase @Inject constructor(
    private val instanceSearchAdapter: InstanceSearchAdapter,
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) : IStatusProviderSearchUseCase {

    override suspend fun invoke(query: String): Result<List<StatusProviderSearchResult>> {
        val url = ActivityPubUrl.create(query) ?: return Result.success(emptyList())
        val client = obtainActivityPubClientUseCase(url.host)
        return client.instanceRepo
            .getInstanceInformation()
            .map { listOf(instanceSearchAdapter.adapt(it)) }
    }
}
