package com.zhangke.utopia.activitypub.app.internal.utils

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class ActivityPubInteractiveHandler @Inject constructor(
    private val statusInteractive: StatusInteractiveUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) {

    suspend fun onStatusInteractive(
        status: Status,
        uiInteraction: StatusUiInteraction,
    ): ActivityPubInteractiveHandleResult {
        val statusInteraction =
            uiInteraction.statusInteraction ?: return ActivityPubInteractiveHandleResult.NoOp
        val result = statusInteractive(status, statusInteraction)
        return if (result.isFailure) {
            val errorMessage = result.exceptionOrNull()?.message?.let { textOf(it) }
            if (errorMessage == null) {
                ActivityPubInteractiveHandleResult.NoOp
            } else {
                ActivityPubInteractiveHandleResult.ShowErrorMessage(errorMessage)
            }
        } else {
            val newStatusEntity = result.getOrThrow()
            val newStatus = statusAdapter.toStatus(newStatusEntity, status.platform)
            ActivityPubInteractiveHandleResult.UpdateStatus(
                statusEntity = newStatusEntity,
                status = buildStatusUiState(newStatus),
            )
        }
    }
}

sealed interface ActivityPubInteractiveHandleResult {

    data class ShowErrorMessage(val message: TextString) : ActivityPubInteractiveHandleResult

    data class UpdateStatus(
        val statusEntity: ActivityPubStatusEntity,
        val status: StatusUiState,
    ) : ActivityPubInteractiveHandleResult

    data object NoOp : ActivityPubInteractiveHandleResult
}
