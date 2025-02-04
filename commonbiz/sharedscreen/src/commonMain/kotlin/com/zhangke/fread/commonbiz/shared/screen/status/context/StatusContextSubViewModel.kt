package com.zhangke.fread.commonbiz.shared.screen.status.context

import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatusContextSubViewModel(
    private val feedsRepo: FeedsRepo,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val role: IdentityRole,
    anchorStatus: Status?,
    blog: Blog?,
    private val blogTranslationUiState: BlogTranslationUiState?,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val _uiState = MutableStateFlow(
        StatusContextUiState(
            contextStatus = emptyList(),
            loading = false,
            needScrollToAnchor = true,
            errorMessage = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val anchorStatus: Status = anchorStatus ?: Status.NewBlog(blog!!)

    private var anchorAuthorFollowing: Boolean? = null

    init {
        _uiState.update {
            it.copy(
                contextStatus = listOf(
                    StatusInContext(
                        type = StatusInContextType.ANCHOR,
                        status = buildStatusUiState(role = role, status = this.anchorStatus)
                    )
                )
            )
        }
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = {
                when (it) {
                    is InteractiveHandleResult.UpdateStatus -> {
                        val innerAnchorStatus = _uiState.value.contextStatus
                            .firstOrNull { status -> status.type == StatusInContextType.ANCHOR }
                        if (innerAnchorStatus?.status != it.status) {
                            updateStatus(it.status)
                        }
                    }

                    is InteractiveHandleResult.DeleteStatus -> {
                        deleteStatus(it.statusId)
                    }

                    is InteractiveHandleResult.UpdateFollowState -> {
                        anchorAuthorFollowing = it.following
                        updateAnchorFollowingState()
                    }
                }
            },
        )
    }

    fun onPageResume() {
        launchInViewModel {
            loadStatusContext()
        }
        loadAnchorFollowingState()
    }

    fun onScrolledToAnchor() {
        _uiState.update { state ->
            state.copy(needScrollToAnchor = false)
        }
    }

    private suspend fun loadStatusContext() {
        _uiState.update { it.copy(loading = true) }
        statusProvider.statusResolver
            .getStatusContext(role, anchorStatus)
            .onSuccess { statusContext ->
                _uiState.update { state ->
                    state.copy(
                        contextStatus = buildContextStatus(fixedAnchorStatus, statusContext),
                        loading = false,
                        errorMessage = null,
                    )
                }
                if (statusContext.status == null) {
                    loadStatus()
                } else {
                    updateAnchorStatus(statusContext.status!!)
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

    private suspend fun loadStatus() {
        statusProvider.statusResolver
            .getStatus(role, anchorStatus.id, anchorStatus.platform)
            .onSuccess { updateAnchorStatus(it) }
    }

    private suspend fun updateAnchorStatus(status: Status) {
        updateStatus(
            buildStatusUiState(
                role = role,
                status = status,
                blogTranslationState = blogTranslationUiState
            ).also { statusUpdater.update(it) }
        )
    }

    private suspend fun buildContextStatus(
        anchorStatus: Status.NewBlog,
        statusContext: StatusContext? = null,
    ): List<StatusInContext> {
        val contextStatus = mutableListOf<StatusInContext>()
        if (statusContext != null) {
            contextStatus += statusContext.ancestors.sortedBy { it.createAt.epochMillis }
                .map { StatusInContext(buildStatusUiState(role, it), StatusInContextType.ANCESTOR) }
        }
        contextStatus += StatusInContext(
            buildStatusUiState(
                role = role,
                status = anchorStatus,
                following = anchorAuthorFollowing,
                blogTranslationState = blogTranslationUiState,
            ),
            StatusInContextType.ANCHOR,
        )
        if (statusContext != null) {
            contextStatus += statusContext.descendants.sortedBy { it.createAt.epochMillis }
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
        updateAnchorFollowingState()
    }

    private fun loadAnchorFollowingState() {
        launchInViewModel {
            statusProvider.statusResolver
                .isFollowing(
                    role = role,
                    target = anchorStatus.intrinsicBlog.author,
                )?.onSuccess { following ->
                    anchorAuthorFollowing = following
                    updateAnchorFollowingState()
                }
        }
    }

    private fun updateAnchorFollowingState() {
        _uiState.update { state ->
            state.copy(
                contextStatus = state.contextStatus.map { item ->
                    if (item.type == StatusInContextType.ANCHOR) {
                        item.copy(status = item.status.copy(following = anchorAuthorFollowing))
                    } else {
                        item
                    }
                }
            )
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
