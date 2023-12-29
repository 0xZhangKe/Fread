package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.status.account.unauthenticatedResult
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusInteraction
import javax.inject.Inject

class StatusInteractiveUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val clientManager: ActivityPubClientManager,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val getStatusSupportInteraction: GetStatusInteractionUseCase,
) {

    suspend operator fun invoke(
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status> {
        val statusId = status.id
        val platform = status.platform
        val activeAccount = accountManager.getActiveAccount() ?: return unauthenticatedResult()
        val statusRepo = clientManager.getClient(activeAccount.baseUrl).statusRepo
        return when (interaction) {
            is StatusInteraction.Like -> {
                if (interaction.liked) {
                    statusRepo.unfavourite(statusId)
                } else {
                    statusRepo.favourite(statusId)
                }
            }

            is StatusInteraction.Forward -> {
                if (interaction.forwarded) {
                    statusRepo.unreblog(statusId)
                } else {
                    statusRepo.reblog(statusId)
                }
            }

            is StatusInteraction.Bookmark -> {
                if (interaction.bookmarked) {
                    statusRepo.unbookmark(statusId)
                } else {
                    statusRepo.bookmark(statusId)
                }
            }

            is StatusInteraction.Delete -> statusRepo.delete(statusId)

            else -> {
                Result.failure(IllegalArgumentException("Unknown interaction: $interaction"))
            }
        }.map { entity ->
            val supportActions = getStatusSupportInteraction(entity)
            activityPubStatusAdapter.toStatus(entity, platform, supportActions)
        }
    }
}
