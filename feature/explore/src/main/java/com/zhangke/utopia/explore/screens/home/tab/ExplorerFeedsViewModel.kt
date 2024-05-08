package com.zhangke.utopia.explore.screens.home.tab

import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.AllInOneRoleResolver
import com.zhangke.utopia.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.explore.model.ExplorerItem
import com.zhangke.utopia.explore.usecase.GetExplorerItemUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ExplorerFeedsViewModel(
    private val type: ExplorerFeedsTabType,
    private val role: IdentityRole,
    private val statusProvider: StatusProvider,
    private val getExplorerItem: GetExplorerItemUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val loadController = CommonLoadableController<ExplorerItem>(viewModelScope)

    val uiState: StateFlow<CommonLoadableUiState<ExplorerItem>> get() = loadController.uiState

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            roleResolver = AllInOneRoleResolver(role),
            onInteractiveHandleResult = { interactiveResult ->
                when (interactiveResult) {
                    is InteractiveHandleResult.UpdateStatus -> {
                        loadController.mutableUiState.update { state ->
                            val dataList = state.dataList.updateStatus(interactiveResult.status)
                            state.copy(dataList = dataList)
                        }
                    }

                    is InteractiveHandleResult.UpdateFollowState -> {
                        val authorUri = interactiveResult.userUri
                        loadController.mutableUiState.update { state ->
                            val dataList = state.dataList.map {
                                if (it is ExplorerItem.ExplorerUser && it.user.uri == authorUri) {
                                    it.copy(following = interactiveResult.following)
                                } else {
                                    it
                                }
                            }
                            state.copy(dataList = dataList)
                        }
                    }
                }
            },
        )
        launchInViewModel {
            loadController.initData(
                getDataFromServer = { getExplorerItem(role, type, 0, "") },
                getDataFromLocal = null,
            )
        }
    }

    fun onRefresh() {
        loadController.onRefresh(false) {
            getExplorerItem(role, type, 0, "")
        }
    }

    fun onLoadMore() {
        val dataList = uiState.value.dataList
        if (dataList.isEmpty()) return
        loadController.onLoadMore {
            getExplorerItem(
                role = role,
                type = type,
                offset = loadController.uiState.value.dataList.size,
                sinceId = dataList.last().id,
            )
        }
    }

    private fun List<ExplorerItem>.updateStatus(newStatus: StatusUiState): List<ExplorerItem> {
        return map { item ->
            if (item is ExplorerItem.ExplorerStatus && item.status.status.id == newStatus.status.id) {
                item.copy(status = newStatus)
            } else {
                item
            }
        }
    }
}
