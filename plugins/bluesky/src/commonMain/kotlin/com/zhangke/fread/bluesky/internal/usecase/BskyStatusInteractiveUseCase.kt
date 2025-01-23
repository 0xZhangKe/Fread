package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
import me.tatarka.inject.annotations.Inject

class BskyStatusInteractiveUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,

) {

    suspend operator fun invoke(
        role: IdentityRole,
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status?> {
        val client = clientManager.getClient(role)
        when(interaction){
//            is StatusInteraction.Like -> {
//                client.putRecord()
//            }
            else -> {}
        }
        return Result.success(status)
    }
}
