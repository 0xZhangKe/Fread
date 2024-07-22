package com.zhangke.fread.feeds.pages.manager.add.pre

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.tryEmitException
import com.zhangke.framework.coroutines.invokeOnCancel
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.ktx.launchInScreenModel
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import com.zhangke.fread.feeds.R
import com.zhangke.fread.feeds.pages.manager.add.mixed.AddMixedFeedsScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.search.SearchContentResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PreAddFeedsViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
) : ScreenModel {

    private val _uiState = MutableStateFlow(PreAddFeedsUiState.default)
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    private val _exitScreenFlow = MutableSharedFlow<Unit>()
    val exitScreenFlow = _exitScreenFlow.asSharedFlow()

    private var searchJob: Job? = null
    private var addContentJob: Job? = null
    private var pendingLoginPlatform: BlogPlatform? = null

    private var selectedContentPlatform: BlogPlatform? = null

    init {
        launchInScreenModel {
            _uiState.update {
                it.copy(allSearchedResult = getSuggestedPlatformSnapshots())
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        if (query.isEmpty()) {
            searchJob?.cancel()
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
        if (searchJob?.isActive == true) return
        doSearch()
    }

    private fun doSearch() {
        searchJob?.cancel()
        searchJob = launchInScreenModel {
            _uiState.update {
                it.copy(
                    searching = true,
                    searchErrorMessage = null,
                )
            }
            statusProvider.searchEngine
                .searchContent(IdentityRole.nonIdentityRole, _uiState.value.query)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            allSearchedResult = list,
                            searching = false,
                            searchErrorMessage = null,
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            searching = false,
                            searchErrorMessage = e.localizedMessage.ifNullOrEmpty { e.message.orEmpty() },
                        )
                    }
                }
        }
        searchJob?.invokeOnCancel {
            _uiState.update {
                it.copy(searching = false)
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

    fun onLoginDialogDismissRequest() {
        _uiState.update { it.copy(showLoginDialog = false) }
    }

    fun onLoginClick() {
        val platform = selectedContentPlatform ?: return
        launchInScreenModel {
            statusProvider.accountManager
                .triggerAuthBySource(platform.baseUrl)
            _exitScreenFlow.emit(Unit)
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
        contentConfigRepo.insertActivityPubContent(platform)
        statusProvider.accountManager
            .checkPlatformLogged(platform)
            .onFailure {
                _exitScreenFlow.emit(Unit)
            }.onSuccess {
                if (it) {
                    _exitScreenFlow.emit(Unit)
                } else {
                    selectedContentPlatform = platform
                    _uiState.update { state ->
                        state.copy(showLoginDialog = true)
                    }
                }
            }
    }

    private suspend fun getSuggestedPlatformSnapshots(): List<SearchContentResult.ActivityPubPlatformSnapshot> {
        return statusProvider.platformResolver
            .getSuggestedPlatformList()
            .map { SearchContentResult.ActivityPubPlatformSnapshot(it) }
    }
}
