package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusInteraction
import javax.inject.Inject

class StatusInteractiveUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(
        status: Status,
        interaction: StatusInteraction,
    ): Result<ActivityPubStatusEntity> {
        val statusId = status.id
        val platform = status.platform
        val statusRepo = clientManager.getClient(platform.baseUrl).statusRepo
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
        }
    }
}
