package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.status.model.Status

class StatusInteractiveUseCase (
    private val clientManager: ActivityPubClientManager,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        status: Status,
        type: StatusActionType,
    ): Result<Status?> {
        val statusId = if (status is Status.Reblog) {
            status.reblog.id
        } else {
            status.id
        }
        val statusRepo = clientManager.getClient(locator).statusRepo
        val blog = status.intrinsicBlog
        val interactionResult = when (type) {
            StatusActionType.LIKE -> {
                if (blog.like.liked == true) {
                    statusRepo.unfavourite(statusId)
                } else {
                    statusRepo.favourite(statusId)
                }
            }

            StatusActionType.FORWARD -> {
                if (blog.forward.forward == true) {
                    statusRepo.unreblog(statusId)
                } else {
                    statusRepo.reblog(statusId)
                }
            }

            StatusActionType.BOOKMARK -> {
                if (blog.bookmark.bookmarked == true) {
                    statusRepo.unbookmark(statusId)
                } else {
                    statusRepo.bookmark(statusId)
                }
            }

            StatusActionType.DELETE -> statusRepo.delete(statusId)

            StatusActionType.PIN -> {
                if (blog.pinned) {
                    statusRepo.unpin(statusId)
                } else {
                    statusRepo.pin(statusId)
                }
            }

            else -> {
                Result.failure(IllegalArgumentException("Unknown interaction: $type"))
            }
        }
        if (interactionResult.isFailure) {
            return Result.failure(interactionResult.exceptionOrNull()!!)
        }
        if (type == StatusActionType.DELETE) {
            return Result.success(null)
        }
        val resultNewStatusEntity = interactionResult.getOrThrow()
        val newStatus = activityPubStatusAdapter.toStatus(
            resultNewStatusEntity,
            status.platform,
        )
        val resultStatus = if (status is Status.Reblog) {
            status.copy(reblog = newStatus.intrinsicBlog)
        } else {
            newStatus
        }
        return Result.success(resultStatus)
    }
}