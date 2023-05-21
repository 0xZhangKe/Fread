package com.zhangke.utopia.activitypubapp.search

import com.google.auto.service.AutoService
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubInstanceOwnerAdapter
import com.zhangke.utopia.activitypubapp.domain.GetHostByUriUseCase
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.status.search.IFindOwnerByUriUseCase
import com.zhangke.utopia.status.source.StatusSourceOwner

@AutoService(IFindOwnerByUriUseCase::class)
class FindActivityPubOwnerByUriUseCase : IFindOwnerByUriUseCase {

    override suspend fun invoke(uri: String): Result<StatusSourceOwner?> {
        val host = GetHostByUriUseCase()(uri) ?: return Result.success(null)
        val client = ObtainActivityPubClientUseCase()(host)
        val adapter = ActivityPubInstanceOwnerAdapter()
        return client.instanceRepo
            .getInstanceInformation()
            .map { adapter.adapt(it) }
    }
}
