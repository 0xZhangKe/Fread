package com.zhangke.utopia.commonbiz.shared.screen.status.context

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatusContextSubViewModel(
    private val feedsRepo: FeedsRepo,
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val role: IdentityRole,
    private val anchorStatus: Status,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(
        StatusContextUiState(
            contextStatus = emptyList(),
            loading = false,
            errorMessage = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    private val _openScreenFlow = MutableSharedFlow<String>()
    val openScreenFlow: SharedFlow<String> get() = _openScreenFlow

    init {
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
                .map { StatusInContext(buildStatusUiState(it), StatusInContextType.ANCESTOR) }
        }
        contextStatus += StatusInContext(
            buildStatusUiState(anchorStatus),
            StatusInContextType.ANCHOR,
        )
        if (statusContext != null) {
            contextStatus += statusContext.descendants.sortedBy { it.datetime }
                .map { StatusInContext(buildStatusUiState(it), StatusInContextType.DESCENDANT) }
        }
        return contextStatus
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        launchInViewModel {
            if (uiInteraction is StatusUiInteraction.Comment) {
                statusProvider.screenProvider
                    .getReplyBlogScreen(role, status.intrinsicBlog)
                    ?.let {
                        _openScreenFlow.emit(it)
                    }
                return@launchInViewModel
            }
            val interaction = uiInteraction.statusInteraction ?: return@launchInViewModel
            statusProvider.statusResolver
                .interactive(role, status, interaction)
                .onSuccess { newStatus ->
                    updateStatus(newStatus)
                }.onFailure { e ->
                    e.message?.takeIf { it.isNotEmpty() }
                        ?.let { message ->
                            _errorMessageFlow.emit(textOf(message))
                        }
                }
        }
    }

    fun onUserInfoClick(author: BlogAuthor) {
        statusProvider.screenProvider
            .getUserDetailRoute(role, author.uri)
            ?.let { launchInViewModel { _openScreenFlow.emit(it) } }
    }

    fun onVote(status: Status, votedOption: List<BlogPoll.Option>) {
        launchInViewModel {
            statusProvider.statusResolver.votePoll(role, status, votedOption)
                .onSuccess {
                    updateStatus(it)
                }.onFailure { e ->
                    e.message?.takeIf { it.isNotEmpty() }
                        ?.let { message ->
                            _errorMessageFlow.emit(textOf(message))
                        }
                }
        }
    }

    private suspend fun updateStatus(newStatus: Status) {
        feedsRepo.updateStatus(newStatus)
        _uiState.update { state ->
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
    }
}
