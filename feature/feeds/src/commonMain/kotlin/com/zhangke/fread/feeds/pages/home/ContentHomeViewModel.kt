package com.zhangke.fread.feeds.pages.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.account.ActiveAccountsSynchronizer
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.feeds.pages.home.feeds.MixedContentScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.model.FreadContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class ContentHomeViewModel @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val statusProvider: StatusProvider,
    private val activeAccountsSynchronizer: ActiveAccountsSynchronizer,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentHomeUiState.default)
    val uiState: StateFlow<ContentHomeUiState> = _uiState

    init {
        launchInViewModel {
            _uiState.update { it.copy(loading = true) }
            val allContent = contentRepo.getAllContent()
            var currentPageIndex = 0
            val lastActiveAccount = activeAccountsSynchronizer.activeAccountUriFlow.value
            if (!lastActiveAccount.isNullOrEmpty()) {
                allContent.indexOfFirst { it.accountUri?.toString() == lastActiveAccount }
                    .takeIf { it >= 0 }
                    ?.let { currentPageIndex = it }
            }
            _uiState.update { currentState ->
                currentState.copy(
                    currentPageIndex = currentPageIndex,
                    contentConfigList = allContent,
                    loading = false,
                )
            }
            activeAccountsSynchronizer.activeAccountUriFlow
                .mapNotNull { it?.takeIf { it.isNotEmpty() } }
                .collect { uri ->
                    val activeIndex = _uiState.value.contentConfigList.indexOfFirst { config ->
                        config.accountUri?.toString() == uri
                    }
                    if (activeIndex >= 0 && activeIndex != _uiState.value.currentPageIndex) {
                        _uiState.update { it.copy(currentPageIndex = activeIndex) }
                    }
                }
        }
        launchInViewModel {
            contentRepo.getAllContentFlow()
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
    }

    fun getContentScreen(
        contentConfig: FreadContent,
        isLatestTab: Boolean,
    ): PagerTab? {
        if (contentConfig is MixedContent) {
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
        launchInViewModel {
            _uiState.value
                .contentConfigList
                .getOrNull(_uiState.value.currentPageIndex)
                ?.accountUri
                ?.toString()
                ?.let { activeAccountsSynchronizer.onAccountSelected(it) }
        }
    }
}
