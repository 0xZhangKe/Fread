package com.zhangke.utopia.explore.screens.search.status

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.explore.screens.search.SearchedResultUiState
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
open class SearchStatusViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SearchedResultUiState<StatusUiState>(
            searching = false,
            resultList = emptyList(),
            loadMoreState = LoadState.Idle,
            errorMessage = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    private var queryJob: Job? = null

    fun onQuery(query: String) {
        _uiState.update { it.copy(searching = true, errorMessage = null) }
        queryJob?.cancel()
        queryJob = launchInViewModel {
            statusProvider.searchEngine
                .search(query)
                .onSuccess {

                }.onFailure {

                }
        }
    }

    fun onLoadMore(){

    }
}
