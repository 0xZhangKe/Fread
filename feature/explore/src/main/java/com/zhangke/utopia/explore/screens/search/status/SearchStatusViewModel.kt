package com.zhangke.utopia.explore.screens.search.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
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
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel(assistedFactory = SearchStatusViewModel.Factory::class)
open class SearchStatusViewModel @AssistedInject constructor(
    private val statusProvider: StatusProvider,
    interactiveHandler: InteractiveHandler,
    buildStatusUiState: BuildStatusUiStateUseCase,
    @Assisted val role: IdentityRole,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(role: IdentityRole): SearchStatusViewModel
    }

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
                .searchStatus(role, query, null)
        }
    }

    fun onLoadMore(query: String) {
        loadStatusController.onLoadMore {
            statusProvider.searchEngine.searchStatus(role, query, it)
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        loadStatusController.onInteractive(role, status, uiInteraction)
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        loadStatusController.onUserInfoClick(role, blogAuthor)
    }

    fun onVoted(status: Status, votedOption: List<BlogPoll.Option>) {
        loadStatusController.onVoted(role, status, votedOption)
    }
}
