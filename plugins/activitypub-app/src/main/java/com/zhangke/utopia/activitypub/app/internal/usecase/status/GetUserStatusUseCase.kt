package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.usecase.client.GetClientUseCase
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
    ): Result<List<Status>> {
        return getClientUseCase().accountRepo.getStatuses(
            id = userUriInsights.userId,
            limit = limit,
            sinceId = sinceId,
        ).map { it.map(activityPubStatusAdapter::adapt) }
    }
}
