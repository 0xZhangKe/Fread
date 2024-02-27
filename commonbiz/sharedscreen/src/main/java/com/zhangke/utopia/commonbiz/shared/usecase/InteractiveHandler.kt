package com.zhangke.utopia.commonbiz.shared.usecase

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.krouter.KRouter
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.model.updateStatus
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.screen.StatusScreenProvider
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class InteractiveHandler @Inject constructor(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) {

    private val screenProvider: StatusScreenProvider get() = statusProvider.screenProvider

    suspend fun onStatusInteractive(
        status: Status,
        uiInteraction: StatusUiInteraction,
    ): InteractiveHandleResult {
        if (uiInteraction is StatusUiInteraction.Comment) {
            return screenProvider.getReplyBlogScreen(status.intrinsicBlog)
                ?.let { KRouter.route<Screen>(it)?.let(InteractiveHandleResult::OpenScreen) }
                ?: InteractiveHandleResult.NoOp
        }
        val interaction = uiInteraction.statusInteraction ?: return InteractiveHandleResult.NoOp
        val result = statusProvider.statusResolver
            .interactive(status, interaction)
            .map { buildStatusUiState(it) }
        return if (result.isFailure) {
            val errorMessage = result.exceptionOrNull()?.message?.let { textOf(it) }
            if (errorMessage == null) {
                InteractiveHandleResult.NoOp
            } else {
                InteractiveHandleResult.ShowErrorMessage(errorMessage)
            }
        } else {
            InteractiveHandleResult.UpdateStatus(result.getOrThrow())
        }
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor): InteractiveHandleResult {
        return screenProvider.getUserDetailRoute(blogAuthor.uri)?.let { route ->
            KRouter.route<Screen>(route)?.let(InteractiveHandleResult::OpenScreen)
        } ?: InteractiveHandleResult.NoOp
    }

    suspend fun onVoted(
        status: Status,
        votedOption: List<BlogPoll.Option>,
    ): InteractiveHandleResult {
        val result = statusProvider.statusResolver.votePoll(status, votedOption)
            .map { buildStatusUiState(it) }
        return if (result.isFailure) {
            val errorMessage = result.exceptionOrNull()?.message?.let { textOf(it) }
            if (errorMessage == null) {
                InteractiveHandleResult.NoOp
            } else {
                InteractiveHandleResult.ShowErrorMessage(errorMessage)
            }
        } else {
            InteractiveHandleResult.UpdateStatus(result.getOrThrow())
        }
    }
}

sealed interface InteractiveHandleResult {

    data class ShowErrorMessage(val message: TextString) : InteractiveHandleResult

    data class OpenScreen(val screen: Screen) : InteractiveHandleResult

    data class UpdateStatus(val status: StatusUiState) : InteractiveHandleResult

    data object NoOp : InteractiveHandleResult
}

suspend fun InteractiveHandleResult.handle(
    uiStatusUpdater: suspend (StatusUiState) -> Unit,
    messageFlow: MutableSharedFlow<TextString>,
    openScreenFlow: MutableSharedFlow<Screen>,
) {
    when (this) {
        is InteractiveHandleResult.OpenScreen -> {
            openScreenFlow.emit(this.screen)
        }

        is InteractiveHandleResult.ShowErrorMessage -> {
            messageFlow.emit(this.message)
        }

        is InteractiveHandleResult.UpdateStatus -> {
            uiStatusUpdater(this.status)
        }

        is InteractiveHandleResult.NoOp -> {}
    }
}

suspend fun InteractiveHandleResult.handle(
    mutableUiState: MutableStateFlow<CommonLoadableUiState<StatusUiState>>,
    messageFlow: MutableSharedFlow<TextString>,
    openScreenFlow: MutableSharedFlow<Screen>,
) {
    handle(
        uiStatusUpdater = { newStatus ->
            mutableUiState.update {
                it.copyObject(
                    dataList = it.dataList.updateStatus(newStatus)
                )
            }
        },
        messageFlow = messageFlow,
        openScreenFlow = openScreenFlow
    )
}
