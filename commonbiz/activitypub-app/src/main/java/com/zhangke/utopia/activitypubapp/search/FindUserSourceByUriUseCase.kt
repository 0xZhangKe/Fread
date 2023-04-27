package com.zhangke.utopia.activitypubapp.search

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountAdapter
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.search.IFindSourceListByUriUseCase
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

internal class FindUserSourceByUriUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val activityPubAccountAdapter: ActivityPubAccountAdapter,
) : IFindSourceListByUriUseCase {

    override suspend fun invoke(uri: String): Result<List<StatusSource>> {
        val webFinger = WebFinger.create(uri) ?: return Result.success(emptyList())
        val client = obtainActivityPubClientUseCase(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .map { listOf(activityPubAccountAdapter.adapt(it)) }
    }
}
