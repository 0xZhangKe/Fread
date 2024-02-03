package com.zhangke.utopia.feeds.pages.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.feeds.pages.home.feeds.MixedContentScreen
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.model.ContentConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ContentHomeViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentHomeUiState(0, emptyList(), emptyList()))
    val uiState: StateFlow<ContentHomeUiState> = _uiState

    private val _openScreenFlow = MutableSharedFlow<String>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    private val _openSelectAccountForPostFlow = MutableSharedFlow<List<LoggedAccount>>()
    val openSelectAccountForPostFlow = _openSelectAccountForPostFlow.asSharedFlow()

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

    fun getContentScreen(contentConfig: ContentConfig): PagerTab? {
        if (contentConfig is ContentConfig.MixedContent) return MixedContentScreen(contentConfig.id)
        return statusProvider.screenProvider.getContentScreen(contentConfig)
    }

    fun onCurrentPageChange(currentPage: Int) {
        val currentState = _uiState.value
        if (currentPage == currentState.currentPageIndex) return
        _uiState.value = currentState.copy(
            currentPageIndex = currentPage,
        )
    }

    fun switchPageIndex(pageIndex: Int) {
        val pageList = _uiState.value.contentConfigList
        if (pageIndex !in pageList.indices) return
        onCurrentPageChange(pageIndex)
    }

    fun onConfigTitleClick(config: ContentConfig) {
        statusProvider.screenProvider
            .getPlatformDetailScreenRoute(config)?.let { route ->
                launchInViewModel { _openScreenFlow.emit(route) }
            }
    }

    fun onPostStatusClick() {
        val accountList = _uiState.value.accountList
        if (accountList.isEmpty()) return
        if (accountList.size == 1) {
            openPostStatusScreen(accountList.first())
        } else {
            launchInViewModel {
                _openSelectAccountForPostFlow.emit(accountList)
            }
        }
    }

    fun onPostStatusAccountClick(account: LoggedAccount) {
        openPostStatusScreen(account)
    }

    private fun openPostStatusScreen(account: LoggedAccount) {
        statusProvider.screenProvider.getPostStatusScreen(
            platform = account.platform,
            accountUri = account.uri,
        )?.let { route ->
            launchInViewModel { _openScreenFlow.emit(route) }
        }
    }
}
