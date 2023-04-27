package com.zhangke.utopia.activitypubapp.search

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubInstanceOwnerAdapter
import com.zhangke.utopia.activitypubapp.domain.GetHostByUriUseCase
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.status.search.IFindOwnerByUriUseCase
import com.zhangke.utopia.status.source.StatusSourceOwner
import javax.inject.Inject

class FindActivityPubOwnerByUriUseCase @Inject constructor(
    private val getHostByUriUseCase: GetHostByUriUseCase,
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val instanceOwnerAdapter: ActivityPubInstanceOwnerAdapter,
) : IFindOwnerByUriUseCase {

    override suspend fun invoke(uri: String): Result<StatusSourceOwner?> {
        val host = getHostByUriUseCase(uri) ?: return Result.success(null)
        val client = obtainActivityPubClientUseCase(host)
        return client.instanceRepo
            .getInstanceInformation()
            .map { instanceOwnerAdapter.adapt(it) }
    }
}
