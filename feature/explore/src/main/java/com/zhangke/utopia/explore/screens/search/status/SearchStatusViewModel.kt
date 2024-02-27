package com.zhangke.utopia.explore.screens.search.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.utils.LoadableStatusController
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class SearchStatusViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    interactiveHandler: InteractiveHandler,
    buildStatusUiState: BuildStatusUiStateUseCase,
) : ViewModel() {

    private val loadStatusController = LoadableStatusController(
        coroutineScope = viewModelScope,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
    )

    val uiState: StateFlow<CommonLoadableUiState<StatusUiState>> get() = loadStatusController.uiState

    val errorMessageFlow: SharedFlow<TextString> = loadStatusController.errorMessageFlow

    val openScreenFlow: SharedFlow<Any> get() = loadStatusController.openScreenFlow

    fun onRefresh(query: String) {
        loadStatusController.onRefresh {
            statusProvider.searchEngine
                .searchStatus(query, null)
        }
    }

    fun onLoadMore(query: String) {
        loadStatusController.onLoadMore {
            statusProvider.searchEngine.searchStatus(query, it)
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        loadStatusController.onInteractive(status, uiInteraction)
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        loadStatusController.onUserInfoClick(blogAuthor)
    }

    fun onVoted(status: Status, votedOption: List<BlogPoll.Option>) {
        loadStatusController.onVoted(status, votedOption)
    }
}
