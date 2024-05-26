package com.zhangke.utopia.feeds.pages.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.feeds.pages.home.feeds.MixedContentScreen
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ContentHomeViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentHomeUiState(0, emptyList(), emptyList()))
    val uiState: StateFlow<ContentHomeUiState> = _uiState

    init {
        launchInViewModel {
            contentConfigRepo.getAllConfigFlow()
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
