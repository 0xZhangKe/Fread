package com.zhangke.utopia.feeds.pages.home.drawer

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ContentHomeViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<LoadableState<ContentHomeDrawerUiState>>(LoadableState.idle())
    val uiState: StateFlow<LoadableState<ContentHomeDrawerUiState>> get() = _uiState

    init {
        loadAllContentConfig()
    }

    private fun loadAllContentConfig() {
        if (_uiState.value.isLoading) return
        launchInViewModel {
            _uiState.value = LoadableState.loading()
            val allConfig = try {
                contentConfigRepo.getAllConfig()
            } catch (e: Throwable) {
                _uiState.value = LoadableState.failed(e)
                return@launchInViewModel
            }
            _uiState.value = LoadableState.success(ContentHomeDrawerUiState(allConfig))
        }
    }
}
