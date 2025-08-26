package com.zhangke.fread.activitypub.app.internal.screen.add.select

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.coroutines.invokeOnCancel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.screen.add.AddActivityPubContentScreen
import com.zhangke.fread.common.onboarding.OnboardingComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class SelectPlatformViewModel @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val onboardingComponent: OnboardingComponent,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectPlatformUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _openNewPageFlow = MutableSharedFlow<Screen>()
    val openNewPageFlow = _openNewPageFlow.asSharedFlow()

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow = _finishPageFlow.asSharedFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    private var queryJob: Job? = null
    private var loadingPlatformForAddJob: Job? = null

    init {
        onboardingComponent.clearState()
        launchInViewModel {
            platformRepo.getSuggestedPlatformSnapshotList()
                .map { SearchPlatformResult.SearchedSnapshot(it) }
                .let { snapshots ->
                    _uiState.update { it.copy(platformSnapshotList = snapshots) }
                }
        }
    }

    fun onPageResumed(uiScope: CoroutineScope) {
        uiScope.launch {
            onboardingComponent.onboardingFinishedFlow.collect {
                _finishPageFlow.emit(Unit)
            }
        }
    }

    fun onSearchClick() {
        doSearch(_uiState.value.query)
    }

    fun onQueryChanged(query: String) {
        if (query == _uiState.value.query) return
        _uiState.update { it.copy(query = query) }
        if (query.isEmpty()) {
            if (queryJob?.isActive == true) queryJob?.cancel()
            _uiState.update { it.copy(searchedResult = emptyList(), querying = false) }
            return
        }
        doSearch(query)
    }

    private fun doSearch(query: String) {
        if (queryJob?.isActive == true) queryJob?.cancel()
        queryJob = launchInViewModel {
            _uiState.update { it.copy(querying = true) }
            val localResult = platformRepo.searchPlatformSnapshotFromLocal(query)
                .map { SearchPlatformResult.SearchedSnapshot(it) }
            _uiState.update { it.copy(searchedResult = localResult) }
            val platformAsUrl = FormalBaseUrl.parse(query)
                ?.let { platformRepo.getPlatform(it).getOrNull() }
                ?.let { SearchPlatformResult.SearchedPlatform(it) }
            if (platformAsUrl != null) {
                _uiState.update { it.copy(searchedResult = it.searchedResult + platformAsUrl) }
            }
            platformRepo.searchPlatformFromServer(query)
                .map { list -> list.map { SearchPlatformResult.SearchedSnapshot(it) } }
                .onSuccess { result ->
                    _uiState.update { it.copy(searchedResult = it.searchedResult + result) }
                }
            _uiState.update { it.copy(querying = false) }
        }
        queryJob?.invokeOnCancel {
            _uiState.update { it.copy(querying = false) }
        }
    }

    fun onResultClick(result: SearchPlatformResult) {
        when (result) {
            is SearchPlatformResult.SearchedPlatform -> {
                launchInViewModel {
                    _openNewPageFlow.emit(AddActivityPubContentScreen(result.platform))
                }
            }

            is SearchPlatformResult.SearchedSnapshot -> {
                if (loadingPlatformForAddJob?.isActive == true) {
                    loadingPlatformForAddJob?.cancel()
                }
                loadingPlatformForAddJob = launchInViewModel {
                    val baseUrl = FormalBaseUrl.parse(result.snapshot.domain)
                    if (baseUrl == null) {
                        _snackBarMessage.emit(textOf("Invalid platform domain: ${result.snapshot.domain}"))
                        return@launchInViewModel
                    }
                    _uiState.update { it.copy(loadingPlatformForAdd = true) }
                    platformRepo.getPlatform(baseUrl)
                        .onSuccess { platform ->
                            _uiState.update { it.copy(loadingPlatformForAdd = false) }
                            _openNewPageFlow.emit(AddActivityPubContentScreen(platform))
                        }.onFailure { t ->
                            _uiState.update { it.copy(loadingPlatformForAdd = false) }
                            _snackBarMessage.emitTextMessageFromThrowable(t)
                        }
                }
            }
        }
    }

    fun onLoadingPlatformForAddCancel() {
        loadingPlatformForAddJob?.cancel()
    }
}
