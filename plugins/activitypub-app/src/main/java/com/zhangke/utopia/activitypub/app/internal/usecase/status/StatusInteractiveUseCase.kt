package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusInteraction
import javax.inject.Inject

class StatusInteractiveUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status> {
        val statusId = if (status is Status.Reblog) {
            status.reblog.id
        } else {
            status.id
        }
        val statusRepo = clientManager.getClient(role).statusRepo
        val interactionResult = when (interaction) {
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
        if (interactionResult.isFailure) {
            return Result.failure(interactionResult.exceptionOrNull()!!)
        }
        val resultNewStatusEntity = interactionResult.getOrThrow()
        val newStatus = activityPubStatusAdapter.toStatus(
            resultNewStatusEntity,
            status.platform,
        )
        val resultStatus = if (status is Status.Reblog) {
            status.copy(
                reblog = newStatus.intrinsicBlog,
                supportInteraction = newStatus.supportInteraction,
            )
        } else {
            newStatus
        }
        return Result.success(resultStatus)
    }
}
