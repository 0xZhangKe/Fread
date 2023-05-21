package com.zhangke.utopia.activitypubapp.search

import com.google.auto.service.AutoService
import com.zhangke.utopia.activitypubapp.adapter.InstanceSearchAdapter
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.search.IStatusProviderSearchUseCase
import com.zhangke.utopia.status.search.StatusProviderSearchResult

@AutoService(IStatusProviderSearchUseCase::class)
class SourceOwnerSearchUseCase : IStatusProviderSearchUseCase {

    override suspend fun invoke(query: String): Result<List<StatusProviderSearchResult>> {
        val url = ActivityPubUrl.create(query) ?: return Result.success(emptyList())
        val client = ObtainActivityPubClientUseCase()(url.host)
        val instanceSearchAdapter = InstanceSearchAdapter()
        return client.instanceRepo
            .getInstanceInformation()
            .map { listOf(instanceSearchAdapter.adapt(it)) }
    }
}
