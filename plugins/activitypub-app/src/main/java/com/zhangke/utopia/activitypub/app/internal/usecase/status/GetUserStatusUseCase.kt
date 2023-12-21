package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.usecase.client.GetClientUseCase
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class GetUserStatusUseCase @Inject constructor(
    private val getClientUseCase: GetClientUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) {

    suspend operator fun invoke(
        userUriInsights: UserUriInsights,
        limit: Int,
        sinceId: String?,
        maxId: String?,
    ): Result<List<Status>> {
        return getClientUseCase(userUriInsights.webFinger.host.toBaseUrl())
            .accountRepo.getStatuses(
                id = userUriInsights.userId,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            ).map { it.map(activityPubStatusAdapter::toStatus) }
    }
}
