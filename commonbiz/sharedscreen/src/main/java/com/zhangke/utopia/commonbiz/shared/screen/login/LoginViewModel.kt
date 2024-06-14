package com.zhangke.utopia.commonbiz.shared.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.tryEmitException
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.PlatformSnapshot
import com.zhangke.utopia.status.search.SearchEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState("", emptyList(), false))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _hideScreenFlow = MutableSharedFlow<Unit>()
    val hideScreenFlow = _hideScreenFlow.asSharedFlow()

    private val searchEngine: SearchEngine get() = statusProvider.searchEngine

    init {
        launchInViewModel {
            _uiState.update { it.copy(platformList = getSuggestedPlatformSnapshots()) }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        searchByCurrentQuery()
    }

    fun onSearchClick() {
        searchByCurrentQuery()
    }

    fun onDismissRequest() {
        _uiState.update { it.copy(loading = false) }
    }

    private fun searchByCurrentQuery() {
        viewModelScope.launch {
            val query = _uiState.value.query
            if (query.isEmpty()) {
                _uiState.update {
                    it.copy(platformList = getSuggestedPlatformSnapshots())
                }
                return@launch
            }
            val snapshotList = searchEngine.searchPlatformSnapshot(query)
                .map { SearchPlatformForLogin.Snapshot(it) }
            _uiState.update { it.copy(platformList = snapshotList) }
            searchEngine.searchPlatform(query, 0)
                .onSuccess { list ->
                    val platformList = list.map { SearchPlatformForLogin.Platform(it) }
                    val currentList = _uiState.value.platformList.toMutableList()
                    currentList.addAll(0, platformList)
                    _uiState.update {
                        it.copy(platformList = currentList)
                    }
                }
        }
    }

    fun onSnapshotClick(snapshot: PlatformSnapshot) {
        launchInViewModel {
            _uiState.update { it.copy(loading = true) }
            statusProvider.platformResolver
                .resolve(snapshot)
                .onFailure {
                    _uiState.update { state -> state.copy(loading = false) }
                    _snackBarMessageFlow.tryEmitException(it)
                }.onSuccess {
                    _uiState.update { state -> state.copy(loading = false) }
                    _hideScreenFlow.emit(Unit)
                    statusProvider.accountManager.triggerAuthBySource(it.baseUrl)
                }
        }
    }

    fun onPlatformClick(platform: BlogPlatform) {
        launchInViewModel {
            _hideScreenFlow.emit(Unit)
            statusProvider.accountManager.triggerAuthBySource(platform.baseUrl)
        }
    }

    private suspend fun getSuggestedPlatformSnapshots(): List<SearchPlatformForLogin.Snapshot> {
        return statusProvider.platformResolver
            .getSuggestedPlatformList()
            .map { SearchPlatformForLogin.Snapshot(it) }
    }
}
