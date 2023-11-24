package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.zhangke.framework.collections.remove
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PostStatusViewModel @Inject constructor(
    private val statusProvider: StatusProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoadableState.loading<PostStatusUiState>())
    val uiState: StateFlow<LoadableState<PostStatusUiState>> = _uiState.asStateFlow()

    init {
        launchInViewModel {
            val accountManager = statusProvider.accountManager
            val loggedAccount = accountManager.getLoggedAccount()
            val allLoggedAccount =
                statusProvider.accountManager.getAllLoggedAccount().getOrNull() ?: emptyList()
            if (loggedAccount == null) {
                _uiState.updateToFailed(IllegalStateException("Not login!"))
            } else {
                _uiState.value = LoadableState.success(
                    PostStatusUiState(
                        account = loggedAccount,
                        availableAccountList = allLoggedAccount,
                        content = "",
                        mediaList = emptyList(),
                        sensitive = false,
                        language = "",
                    )
                )
            }
        }
    }

    fun onContentChanged(inputtedText: String) {
        _uiState.updateOnSuccess {
            it.copy(content = inputtedText)
        }
    }

    fun onSensitiveClick() {
        _uiState.updateOnSuccess {
            it.copy(sensitive = !it.sensitive)
        }
    }

    fun onMediaSelected(list: List<Uri>) {
        _uiState.updateOnSuccess {
            it.copy(
                mediaList = it.mediaList.plus(list),
            )
        }
    }

    fun onMediaDeleteClick(uri: Uri) {
        _uiState.updateOnSuccess { state ->
            state.copy(mediaList = state.mediaList.remove { it == uri })
        }
    }

    fun onPostClick() {
    }
}
