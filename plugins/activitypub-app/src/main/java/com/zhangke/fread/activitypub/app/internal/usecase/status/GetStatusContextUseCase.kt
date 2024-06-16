package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import javax.inject.Inject

class GetStatusContextUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val convertToStatusContext: ConvertToStatusContextUseCase,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        status: Status,
    ): Result<StatusContext> {
        return clientManager.getClient(role)
            .statusRepo
            .getStatusContext(status.id)
            .map { convertToStatusContext(it, status.platform) }
    }
}
