package com.zhangke.fread.commonbiz.shared.screen.status.context

import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.mixed.MixedStatusRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.status.model.DescendantStatus
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatusContextSubViewModel(
    private val mixedStatusRepo: MixedStatusRepo,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val locator: PlatformLocator,
    anchorStatus: StatusUiState?,
    blog: Blog?,
    private val blogTranslationUiState: BlogTranslationUiState?,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
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

    private val anchorStatus: StatusUiState =
        anchorStatus ?: statusUiStateAdapter.toStatusUiStateSnapshot(
            locator = locator,
            status = Status.NewBlog(blog!!),
            blogTranslationState = blogTranslationUiState,
        )

    private var anchorAuthorFollowing: Boolean? = null

    init {
        _uiState.update {
            it.copy(
                contextStatus = listOf(
                    StatusInContext(type = StatusInContextType.ANCHOR, status = this.anchorStatus)
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
            .getStatusContext(locator, anchorStatus.status)
            .map { statusContext ->
                val status = statusContext.status?.let {
                    statusUpdater.update(it)
                    it.copy(
                        following = anchorAuthorFollowing ?: it.following,
                        blogTranslationState = blogTranslationUiState ?: it.blogTranslationState
                    )
                } ?: loadStatus() ?: anchorStatus
                statusContext.copy(
                    status = status.copy(
                        following = anchorAuthorFollowing ?: status.following,
                    )
                )
            }
            .onSuccess { statusContext ->
                _uiState.update { state ->
                    state.copy(
                        contextStatus = buildContextStatus(statusContext),
                        loading = false,
                        errorMessage = null,
                    )
                }
                statusUpdater.update(statusContext.status!!)
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        errorMessage = it.toTextStringOrNull(),
                    )
                }
            }
    }

    private suspend fun loadStatus(): StatusUiState? {
        return statusProvider.statusResolver
            .getStatus(locator, anchorStatus.status.intrinsicBlog, anchorStatus.status.platform)
            .map {
                it.copy(
                    following = anchorAuthorFollowing ?: it.following,
                    blogTranslationState = blogTranslationUiState ?: it.blogTranslationState,
                )
            }
            .onSuccess { statusUpdater.update(it) }
            .getOrNull()
    }

    private fun buildContextStatus(
        statusContext: StatusContext,
    ): List<StatusInContext> {
        val contextStatus = mutableListOf<StatusInContext>()
        contextStatus += statusContext.ancestors.sortedBy { it.status.createAt.epochMillis }
            .map { StatusInContext(it, StatusInContextType.ANCESTOR) }
        contextStatus += StatusInContext(
            statusContext.status!!,
            StatusInContextType.ANCHOR,
        )
        for (descendant in statusContext.descendants) {
            contextStatus.addAll(descendant.expandToContextStatus(null))
        }
        return contextStatus
    }

    private fun DescendantStatus.expandToContextStatus(
        parentType: StatusInContextType?,
    ): List<StatusInContext> {
        val type = if (this.descendantStatus != null) {
            if (parentType == null) {
                StatusInContextType.DESCENDANT_ANCHOR
            } else {
                StatusInContextType.DESCENDANT_WITH_ANCESTOR_DESCENDANT
            }
        } else {
            if (parentType == null) {
                StatusInContextType.DESCENDANT
            } else {
                StatusInContextType.DESCENDANT_WITH_ANCESTOR
            }
        }
        val list = mutableListOf<StatusInContext>()
        val statusInContext = StatusInContext(status, type)
        list.add(statusInContext)
        if (this.descendantStatus != null) {
            list.addAll(this.descendantStatus!!.expandToContextStatus(type))
        }
        return list
    }

    private suspend fun updateStatus(newStatus: StatusUiState) {
        mixedStatusRepo.updateStatus(newStatus)
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
                    locator = locator,
                    target = anchorStatus.status.intrinsicBlog.author,
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
