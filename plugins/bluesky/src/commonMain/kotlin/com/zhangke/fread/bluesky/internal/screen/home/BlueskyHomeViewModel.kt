package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.usecase.UpdateHomeTabUseCase
import com.zhangke.fread.bluesky.internal.utils.createPlatformLocator
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class BlueskyHomeViewModel(
    private val contentId: String,
    private val contentRepo: FreadContentRepo,
    private val updateHomeTab: UpdateHomeTabUseCase,
    private val accountManager: BlueskyLoggedAccountManager,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(BlueskyHomeUiState.default(initializing = true))
    val uiState: StateFlow<BlueskyHomeUiState> = _uiState

    private var observeAccountJob: Job? = null

    init {
        launchInViewModel {
            contentRepo.getContentFlow(contentId)
                .distinctUntilChanged()
                .map { it as? BlueskyContent }
                .collect { config ->
                    if (config != null) {
                        val locator = createPlatformLocator(config)
                        _uiState.update { state ->
                            state.copy(
                                locator = locator,
                                content = config,
                            )
                        }
                        startObserveAccount(locator)
                    } else {
                        _uiState.update { state ->
                            state.copy(errorMessage = "Cant find validate config by id: $contentId")
                        }
                    }
                }
        }
        launchInViewModel {
            delay(200)
            _uiState.update { it.copy(initializing = false) }
        }
    }

    private fun startObserveAccount(locator: PlatformLocator) {
        observeAccountJob?.cancel()
        if (locator.accountUri == null) {
            _uiState.update { it.copy(account = null) }
            return
        }
        observeAccountJob = launchInViewModel {
            accountManager.getAccountFlow(locator.accountUri!!)
                .distinctUntilChanged()
                .collect { account ->
                    val accountSize = accountManager.getAllAccount().size
                    _uiState.update {
                        it.copy(account = account, showAccountInTopBar = accountSize > 1)
                    }
                    updateHomeTab(contentId, locator)
                }
        }
    }
}
