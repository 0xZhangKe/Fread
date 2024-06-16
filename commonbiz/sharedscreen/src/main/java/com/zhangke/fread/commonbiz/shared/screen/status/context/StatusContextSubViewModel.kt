package com.zhangke.fread.commonbiz.shared.screen.status.context

import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatusContextSubViewModel(
    private val feedsRepo: FeedsRepo,
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val role: IdentityRole,
    private val anchorStatus: Status,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val _uiState = MutableStateFlow(
        StatusContextUiState(
            contextStatus = emptyList(),
            loading = false,
            errorMessage = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = {
                when (it) {
                    is InteractiveHandleResult.UpdateStatus -> {
                        updateStatus(it.status)
                    }

                    is InteractiveHandleResult.DeleteStatus -> {
                        deleteStatus(it.statusId)
                    }

                    is InteractiveHandleResult.UpdateFollowState -> {
                        // no-op
                    }
                }
            },
        )
        launchInViewModel {
            val fixedAnchorStatus = refactorToNewBlog(anchorStatus)
            _uiState.value = _uiState.value.copy(
                loading = true,
                contextStatus = buildContextStatus(fixedAnchorStatus),
            )
            statusProvider.statusResolver
                .getStatusContext(role, fixedAnchorStatus)
                .onSuccess { statusContext ->
                    _uiState.update { state ->
                        state.copy(
                            contextStatus = buildContextStatus(fixedAnchorStatus, statusContext),
                            loading = false,
                            errorMessage = null,
                        )
                    }
                }.onFailure {
                    _uiState.update { state ->
                        state.copy(
                            contextStatus = buildContextStatus(fixedAnchorStatus),
                            loading = false,
                            errorMessage = it.toTextStringOrNull(),
                        )
                    }
                }
        }
    }

    private fun buildContextStatus(
        anchorStatus: Status.NewBlog,
        statusContext: StatusContext? = null,
    ): List<StatusInContext> {
        val contextStatus = mutableListOf<StatusInContext>()
        if (statusContext != null) {
            contextStatus += statusContext.ancestors.sortedBy { it.datetime }
                .map { StatusInContext(buildStatusUiState(role, it), StatusInContextType.ANCESTOR) }
        }
        contextStatus += StatusInContext(
            buildStatusUiState(role, anchorStatus),
            StatusInContextType.ANCHOR,
        )
        if (statusContext != null) {
            contextStatus += statusContext.descendants.sortedBy { it.datetime }
                .map {
                    StatusInContext(
                        buildStatusUiState(role, it),
                        StatusInContextType.DESCENDANT
                    )
                }
        }
        return contextStatus
    }

    private suspend fun updateStatus(newStatus: StatusUiState) {
        feedsRepo.updateStatus(newStatus.status)
        _uiState.update { state ->
            val contextStatus = state.contextStatus.map { item ->
                item.copy(
                    status = if (item.status.status.intrinsicBlog.id == newStatus.status.intrinsicBlog.id) {
                        newStatus
                    } else {
                        item.status
                    }
                )
            }
            state.copy(contextStatus = contextStatus)
        }
    }

    private fun deleteStatus(statusId: String) {
        _uiState.update { state ->
            state.copy(
                contextStatus = state.contextStatus.filter { it.status.status.id != statusId }
            )
        }
    }
}
