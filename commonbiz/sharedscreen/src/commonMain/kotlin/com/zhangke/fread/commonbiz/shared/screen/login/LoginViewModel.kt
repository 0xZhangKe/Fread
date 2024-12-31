package com.zhangke.fread.commonbiz.shared.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.search.SearchEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class LoginViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState("", emptyList()))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _hideScreenFlow = MutableSharedFlow<Unit>()
    val hideScreenFlow = _hideScreenFlow.asSharedFlow()

    private val searchEngine: SearchEngine get() = statusProvider.searchEngine
    private var searchJob: Job? = null

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

    private fun searchByCurrentQuery() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val query = _uiState.value.query
            if (query.isEmpty()) {
                _uiState.update {
                    it.copy(platformList = getSuggestedPlatformSnapshots())
                }
                return@launch
            }
            _uiState.update { it.copy(platformList = emptyList()) }
            searchEngine.searchAuthablePlatform(query)
                .collect { (q, list) ->
                    if (q == query) {
                        _uiState.update { it.copy(platformList = list) }
                    }
                }
        }
    }

    fun onSnapshotClick(
        snapshot: PlatformSnapshot,
        openOauthPage: (String) -> Unit,
    ) {
        val baseUrl = FormalBaseUrl.parse(snapshot.domain) ?: return
        launchInViewModel {
            _hideScreenFlow.emit(Unit)
            statusProvider.accountManager.triggerAuthBySource(baseUrl, openOauthPage)
        }
    }

    private suspend fun getSuggestedPlatformSnapshots(): List<PlatformSnapshot> {
        return statusProvider.platformResolver
            .getSuggestedPlatformList()
    }
}
