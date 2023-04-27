package com.zhangke.utopia.activitypubapp.search

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountSearchAdapter
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.search.IStatusProviderSearchUseCase
import com.zhangke.utopia.status.search.StatusProviderSearchResult
import javax.inject.Inject

internal class UserSourceSearchUseCase @Inject constructor(
    private val accountSearchAdapter: ActivityPubAccountSearchAdapter,
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) : IStatusProviderSearchUseCase {

    override suspend fun invoke(query: String): Result<List<StatusProviderSearchResult>> {
        val webFinger = WebFinger.create(query) ?: return Result.success(emptyList())
        return resolveByWebFinger(webFinger).map { listOf(it) }
    }

    private suspend fun resolveByWebFinger(
        webFinger: WebFinger
    ): Result<StatusProviderSearchResult> {
        val client = obtainActivityPubClientUseCase(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .map { accountSearchAdapter.adapt(webFinger, it) }
    }
}
