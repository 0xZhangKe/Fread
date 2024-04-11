package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import javax.inject.Inject

class GetStatusContextUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val convertToStatusContext: ConvertToStatusContextUseCase,
) {

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        status: Status,
    ): Result<StatusContext> {
        return clientManager.getClient(baseUrl)
            .statusRepo
            .getStatusContext(status.id)
            .map { convertToStatusContext(it, status.platform) }
    }
}
