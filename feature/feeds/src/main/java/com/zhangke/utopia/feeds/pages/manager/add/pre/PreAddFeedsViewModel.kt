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
            loading = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    private val _loginRecommendPlatform = MutableSharedFlow<BlogPlatform>()
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
        launchInScreenModel {
            _uiState.update {
                it.copy(allSearchedResult = getSuggestedPlatformSnapshots())
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        if (query.isEmpty()) {
            launchInScreenModel {
                _uiState.update {
                    it.copy(allSearchedResult = getSuggestedPlatformSnapshots())
                }
            }
            return
        }
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
                    _uiState.update { it.copy(loading = true) }
                    statusProvider.platformResolver.resolve(result.platform)
                        .onFailure {
                            _uiState.update { state -> state.copy(loading = false) }
                            _snackBarMessageFlow.tryEmitException(it)
                        }.onSuccess {
                            _uiState.update { state -> state.copy(loading = false) }
                            onAddActivityPubContent(it)
                        }
                }
            }
        }
    }

    fun onLoadingDismissRequest() {
        _uiState.update { it.copy(loading = false) }
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
                    _loginRecommendPlatform.emit(platform)
                }
            }
    }

    private suspend fun performAddActivityPubContent(platform: BlogPlatform) {
        contentConfigRepo.insertActivityPubContent(platform)
        _addContentSuccessFlow.emit(Unit)
    }

    private suspend fun getSuggestedPlatformSnapshots(): List<SearchContentResult.ActivityPubPlatformSnapshot> {
        return statusProvider.platformResolver
            .getSuggestedPlatformList()
            .map { SearchContentResult.ActivityPubPlatformSnapshot(it) }
    }
}
