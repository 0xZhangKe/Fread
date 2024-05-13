package com.zhangke.utopia.feeds.pages.manager.add.pre

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.tryEmitException
import com.zhangke.framework.ktx.launchInScreenModel
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.feeds.pages.manager.add.mixed.AddMixedFeedsScreen
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.search.SearchContentResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PreAddFeedsViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        PreAddFeedsUiState(
            query = "",
            allSearchedResult = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    private val _loginRecommendPlatform = MutableSharedFlow<List<BlogPlatform>>()
    val loginRecommendPlatform = _loginRecommendPlatform.asSharedFlow()

    private val _addContentSuccessFlow = MutableSharedFlow<Unit>()
    val addContentSuccessFlow: SharedFlow<Unit> get() = _addContentSuccessFlow

    private var searchJob: Job? = null
    private var addContentJob: Job? = null
    private var pendingLoginPlatform: BlogPlatform? = null

    init {
        launchInScreenModel {
            val initAccountList = statusProvider.accountManager.getAllLoggedAccount()
            statusProvider.accountManager
                .getAllAccountFlow()
                .collect { currentAccountList ->
                    if (initAccountList.isNotEmpty()) return@collect
                    if (currentAccountList.isEmpty()) return@collect
                    pendingLoginPlatform?.let { performAddActivityPubContent(it) }
                }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        doSearch()
    }

    fun onSearchClick() {
        doSearch(true)
    }

    private fun doSearch(showErrorMessage: Boolean = false) {
        searchJob?.cancel()
        searchJob = launchInScreenModel {
            statusProvider.searchEngine
                .searchContent(IdentityRole.nonIdentityRole, _uiState.value.query)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(allSearchedResult = list)
                    }
                }.onFailure { e ->
                    if (showErrorMessage) {
                        e.message?.let { textOf(it) }
                            ?.let { _snackBarMessageFlow.emit(it) }
                    }
                }
        }
    }

    fun onContentClick(result: SearchContentResult) {
        pendingLoginPlatform = null
        if (addContentJob?.isActive == true) return
        addContentJob = launchInScreenModel {
            when (result) {
                is SearchContentResult.Source -> {
                    _openScreenFlow.emit(AddMixedFeedsScreen(result.source))
                }

                is SearchContentResult.ActivityPubPlatform -> {
                    onAddActivityPubContent(result.platform)
                }

                is SearchContentResult.ActivityPubPlatformSnapshot -> {
                    statusProvider.platformResolver.resolve(result.platform)
                        .onFailure {
                            _snackBarMessageFlow.tryEmitException(it)
                        }.onSuccess {
                            onAddActivityPubContent(it)
                        }
                }
            }
        }
    }

    private suspend fun onAddActivityPubContent(platform: BlogPlatform) {
        val existsConfig = contentConfigRepo.getAllConfig()
            .filterIsInstance<ContentConfig.ActivityPubContent>()
            .firstOrNull { it.baseUrl == platform.baseUrl }
        if (existsConfig != null) {
            _snackBarMessageFlow.emit(textOf(R.string.add_feeds_page_empty_content_exist))
            return
        }
        statusProvider.accountManager
            .checkPlatformLogged(platform)
            .onFailure {
                _snackBarMessageFlow.tryEmitException(it)
            }.onSuccess {
                if (it) {
                    performAddActivityPubContent(platform)
                } else {
                    pendingLoginPlatform = platform
                    _loginRecommendPlatform.emit(listOf(platform))
                }
            }
    }

    private suspend fun performAddActivityPubContent(platform: BlogPlatform) {
        val contentConfig = ContentConfig.ActivityPubContent(
            id = 0,
            order = contentConfigRepo.generateNextOrder(),
            name = platform.name,
            baseUrl = platform.baseUrl,
            showingTabList = buildInitialTabConfigList(),
            hiddenTabList = emptyList(),
        )
        contentConfigRepo.insert(contentConfig)
        _addContentSuccessFlow.emit(Unit)
    }

    private fun buildInitialTabConfigList(): List<ContentConfig.ActivityPubContent.ContentTab> {
        val tabList = mutableListOf<ContentConfig.ActivityPubContent.ContentTab>()
        tabList += ContentConfig.ActivityPubContent.ContentTab.HomeTimeline(0)
        tabList += ContentConfig.ActivityPubContent.ContentTab.LocalTimeline(1)
        tabList += ContentConfig.ActivityPubContent.ContentTab.PublicTimeline(2)
        tabList += ContentConfig.ActivityPubContent.ContentTab.Trending(3)
        return tabList
    }
}
