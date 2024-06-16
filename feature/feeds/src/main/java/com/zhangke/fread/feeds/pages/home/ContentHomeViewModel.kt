package com.zhangke.fread.feeds.pages.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import com.zhangke.fread.feeds.pages.home.feeds.MixedContentScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.ContentConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ContentHomeViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentHomeUiState.default)
    val uiState: StateFlow<ContentHomeUiState> = _uiState

    init {
        launchInViewModel {
            _uiState.update { it.copy(loading = true) }
            val allConfig = contentConfigRepo.getAllConfig()
            _uiState.update { currentState ->
                currentState.copy(
                    currentPageIndex = 0,
                    contentConfigList = allConfig,
                    loading = false,
                )
            }
        }
        launchInViewModel {
            contentConfigRepo.getAllConfigFlow()
                .drop(1)
                .collect {
                    _uiState.update { currentState ->
                        currentState.copy(
                            currentPageIndex = currentState.currentPageIndex.coerceAtMost(it.size - 1),
                            contentConfigList = it,
                        )
                    }
                }
        }
        launchInViewModel {
            statusProvider.accountManager
                .getAllAccountFlow()
                .collect { accountList ->
                    _uiState.update {
                        it.copy(accountList = accountList)
                    }
                }
        }
    }

    fun getContentScreen(
        contentConfig: ContentConfig,
        isLatestTab: Boolean,
    ): PagerTab? {
        if (contentConfig is ContentConfig.MixedContent) {
            return MixedContentScreen(
                configId = contentConfig.id,
                isLatestTab = isLatestTab,
            )
        }
        return statusProvider.screenProvider.getContentScreen(contentConfig, isLatestTab)
    }

    fun onCurrentPageChange(currentPage: Int) {
        val currentState = _uiState.value
        if (currentPage == currentState.currentPageIndex) return
        _uiState.value = currentState.copy(
            currentPageIndex = currentPage,
        )
    }
}
