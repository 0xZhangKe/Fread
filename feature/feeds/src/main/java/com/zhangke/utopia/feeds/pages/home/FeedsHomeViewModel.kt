package com.zhangke.utopia.feeds.pages.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.successDataOrNull
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.repo.FeedsConfigRepo
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FeedsHomeViewModel @Inject constructor(
    private val feedsConfigRepo: FeedsConfigRepo,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoadableState.idle<FeedsHomeUiState>())
    val uiState: StateFlow<LoadableState<FeedsHomeUiState>> get() = _uiState.asStateFlow()

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    val screenProvider = statusProvider.screenProvider

    init {
        loadFeedsConfigList()
    }

    private fun loadFeedsConfigList() {
        launchInViewModel {
            _uiState.value = LoadableState.loading()
            feedsConfigRepo.getAllFeedsConfigFlow()
                .collect { feedsConfigList ->
                    val state = FeedsHomeUiState(
                        selectedIndex = 0,
                        feedsConfigList = feedsConfigList.map {
                            FeedsConfigWithPlatforms(it, emptyList())
                        },
                    )
                    _uiState.value = LoadableState.success(state)
                    val finalFeedsConfigList = feedsConfigList.map { config ->
                        async {
                            val platformList = statusProvider.platformResolver
                                .resolveBySourceUriList(config.sourceUriList)
                                .getOrNull()
                            FeedsConfigWithPlatforms(config, platformList ?: emptyList())
                        }
                    }.awaitAll()
                    _uiState.updateOnSuccess {
                        it.copy(feedsConfigList = finalFeedsConfigList)
                    }
                }
        }
    }

    fun onTabSelected(index: Int) {
        val currentIndex = _uiState.value.successDataOrNull()?.selectedIndex
        if (index == currentIndex) return
        _uiState.updateOnSuccess {
            it.copy(selectedIndex = index)
        }
    }

    fun showErrorMessage(text: TextString) {
        launchInViewModel {
            _errorMessageFlow.emit(text)
        }
    }
}
