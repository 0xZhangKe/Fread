package com.zhangke.utopia.activitypubapp.search

import com.google.auto.service.AutoService
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountAdapter
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.search.IFindSourceListByUriUseCase
import com.zhangke.utopia.status.source.StatusSource

@AutoService(IFindSourceListByUriUseCase::class)
class FindUserSourceByUriUseCase : IFindSourceListByUriUseCase {

    override suspend fun invoke(uri: String): Result<List<StatusSource>> {
        val webFinger = WebFinger.create(uri) ?: return Result.success(emptyList())
        val client = ObtainActivityPubClientUseCase()(webFinger.host)
        val adapter = ActivityPubAccountAdapter()
        return client.accountRepo
            .lookup(webFinger.toString())
            .map { listOf(adapter.adapt(it)) }
    }
}
