package com.zhangke.fread.activitypub.app.internal.screen.user.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
class SearchUserViewModel (
    private val clientManager: ActivityPubClientManager,
    private val locator: PlatformLocator,
    private val onlyFollowing: Boolean,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUserUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onSearchClick() {
        searchJob?.cancel()
        val query = _uiState.value.query
        _uiState.update { it.copy(searching = true) }
        searchJob = launchInViewModel {
            clientManager.getClient(locator)
                .accountRepo
                .search(
                    query = query,
                    resolve = false,
                    following = onlyFollowing,
                ).onSuccess { accounts ->
                    _uiState.update {
                        it.copy(
                            searching = false,
                            accounts = accounts,
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(searching = false)
                    }
                    _snackBarMessage.emitTextMessageFromThrowable(it)
                }
        }
    }
}