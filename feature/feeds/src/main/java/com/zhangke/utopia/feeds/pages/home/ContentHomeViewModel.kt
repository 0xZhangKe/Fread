package com.zhangke.utopia.feeds.pages.home

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.feeds.pages.home.feeds.MixedContentScreen
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ContentHomeViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentHomeUiState(0, emptyList()))
    val uiState: StateFlow<ContentHomeUiState> = _uiState

    init {
        launchInViewModel {
            contentConfigRepo.getAllConfigFlow()
                .collect {
                    val currentState = _uiState.value
                    _uiState.value = currentState.copy(
                        currentPageIndex = currentState.currentPageIndex.coerceAtMost(it.size - 1),
                        contentConfigList = it,
                    )
                }
        }
    }

    fun getContentScreen(contentConfig: ContentConfig): Screen? {
        if (contentConfig is ContentConfig.MixedContent) return MixedContentScreen(contentConfig.id)
        return statusProvider.screenProvider.getContentScreen(contentConfig) as? Screen
    }

    fun onCurrentPageChange(currentPage: Int) {
        val currentState = _uiState.value
        if (currentPage == currentState.currentPageIndex) return
        _uiState.value = currentState.copy(
            currentPageIndex = currentPage,
        )
    }
}
