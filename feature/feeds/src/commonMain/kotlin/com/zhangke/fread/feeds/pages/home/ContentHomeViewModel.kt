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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
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

    private val _switchPageFlow = MutableSharedFlow<Int>(1)
    val switchPageFlow: SharedFlow<Int> = _switchPageFlow

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
                    contentAndTabList = convertContentsToWithTab(allContent),
                    loading = false,
                )
            }
            activeAccountsSynchronizer.activeAccountUriFlow
                .mapNotNull { it?.takeIf { it.isNotEmpty() } }
                .collect { uri ->
                    val activeIndex = _uiState.value.contentAndTabList.indexOfFirst { config ->
                        config.first.accountUri?.toString() == uri
                    }
                    if (activeIndex >= 0 && activeIndex != _uiState.value.currentPageIndex) {
                        _switchPageFlow.emit(activeIndex)
                    }
                }
        }
        launchInViewModel {
            contentRepo.getAllContentFlow()
                .drop(1)
                .map { convertContentsToWithTab(it) }
                .collect {
                    _uiState.update { currentState ->
                        currentState.copy(
                            currentPageIndex = currentState.currentPageIndex.coerceAtMost(it.size - 1),
                            contentAndTabList = it,
                        )
                    }
                }
        }
    }

    fun onCurrentPageChanged(currentPage: Int) {
        val currentState = _uiState.value
        if (currentPage == currentState.currentPageIndex) return
        _uiState.update { it.copy(currentPageIndex = currentPage) }
        launchInViewModel {
            _uiState.value
                .contentAndTabList
                .getOrNull(currentPage)
                ?.first
                ?.accountUri
                ?.toString()
                ?.let { activeAccountsSynchronizer.onAccountSelected(it) }
        }
    }

    private fun convertContentsToWithTab(contents: List<FreadContent>): List<Pair<FreadContent, PagerTab>> {
        return contents.mapIndexed { index, content ->
            content.convertToWithTab(index == contents.lastIndex)
        }
    }

    private fun FreadContent.convertToWithTab(isLatestTab: Boolean): Pair<FreadContent, PagerTab> {
        if (this is MixedContent) {
            return this to MixedContentScreen(
                configId = id,
                isLatestTab = isLatestTab,
            )
        }
        return this to statusProvider.screenProvider.getContentScreen(this, isLatestTab)
    }

    fun onSwitchPageFlowUsed() {
        _switchPageFlow.resetReplayCache()
    }
}
