package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.usecase.baseurl.ChooseBaseUrlUseCase
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import javax.inject.Inject

class GetStatusContextUseCase @Inject constructor(
    private val chooseBaseUrl: ChooseBaseUrlUseCase,
    private val clientManager: ActivityPubClientManager,
    private val convertToStatusContext: ConvertToStatusContextUseCase,
) {

    suspend operator fun invoke(
        status: Status,
    ): Result<StatusContext> {
        return clientManager.getClient(chooseBaseUrl())
            .statusRepo
            .getStatusContext(status.id)
            .map { convertToStatusContext(it, status.platform) }
    }
}
