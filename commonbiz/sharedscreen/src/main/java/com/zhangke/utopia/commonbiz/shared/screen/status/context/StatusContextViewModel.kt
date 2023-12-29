package com.zhangke.utopia.commonbiz.shared.screen.status.context

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.composable.updateToLoading
import com.zhangke.framework.composable.updateToSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatusContextViewModel(
    private val feedsRepo: FeedsRepo,
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ViewModel() {

    lateinit var anchorStatus: Status

    private val _uiState = MutableStateFlow(LoadableState.idle<StatusContextUiState>())
    val uiState: StateFlow<LoadableState<StatusContextUiState>> get() = _uiState

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    fun onPrepares() {
        loadStatusContext()
    }

    private fun loadStatusContext() {
        if (_uiState.value.isLoading) return
        launchInViewModel {
            _uiState.updateToLoading()
            statusProvider.statusResolver
                .getStatusContext(anchorStatus)
                .onSuccess {
                    _uiState.updateToSuccess(it.toUiState())
                }.onFailure {
                    _uiState.updateToFailed(it)
                }
        }
    }

    private fun StatusContext.toUiState(): StatusContextUiState {
        val contextStatus = mutableListOf<StatusInContext>()
        contextStatus += this.ancestors.sortedBy { it.datetime }
            .map { StatusInContext(buildStatusUiState(it), StatusInContextType.ANCESTOR) }
        contextStatus += StatusInContext(
            buildStatusUiState(anchorStatus),
            StatusInContextType.ANCHOR,
        )
        contextStatus += this.descendants.sortedByDescending { it.datetime }
            .map { StatusInContext(buildStatusUiState(it), StatusInContextType.DESCENDANT) }
        return StatusContextUiState(
            contextStatus = contextStatus.filter {
                it.status.status is Status.NewBlog
            },
        )
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        launchInViewModel {
            val interaction = uiInteraction.statusInteraction ?: return@launchInViewModel
            statusProvider.statusResolver
                .interactive(status, interaction)
                .onSuccess { newStatus ->
                    feedsRepo.updateStatus(newStatus)
                    _uiState.updateOnSuccess { state ->
                        val contextStatus = state.contextStatus.map { item ->
                            item.copy(
                                status = if (item.status.status.id == newStatus.id) {
                                    buildStatusUiState(newStatus)
                                } else {
                                    item.status
                                }
                            )
                        }
                        state.copy(contextStatus = contextStatus)
                    }
                }.onFailure {
                    it.message?.takeIf { it.isNotEmpty() }
                        ?.let { message ->
                            _errorMessageFlow.emit(textOf(message))
                        }
                }
        }
    }
}
