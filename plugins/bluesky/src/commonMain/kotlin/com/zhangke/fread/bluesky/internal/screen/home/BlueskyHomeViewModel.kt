package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class BlueskyHomeViewModel(
    private val configId: Long,
    private val contentConfigRepo: ContentConfigRepo,
    private val accountManager: BlueskyLoggedAccountManager,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(BlueskyHomeUiState.default())
    val uiState: StateFlow<BlueskyHomeUiState> = _uiState

    private var observeAccountJob: Job? = null

    init {
        launchInViewModel {
            contentConfigRepo.getConfigFlowById(configId)
                .map { it as? ContentConfig.BlueskyContent }
                .collect { config ->
                    if (config != null) {
                        _uiState.update { state ->
                            state.copy(
                                role = IdentityRole(accountUri = null, baseUrl = config.baseUrl),
                                config = config,
                            )
                        }
                        startObserveAccount(config.baseUrl)
                    } else {
                        _uiState.update { state ->
                            state.copy(errorMessage = "Cant find validate config by id: $configId")
                        }
                    }
                }
        }
    }

    private fun startObserveAccount(baseUrl: FormalBaseUrl) {
        observeAccountJob?.cancel()
        observeAccountJob = launchInViewModel {
            accountManager.getAccountFlow(baseUrl)
                .distinctUntilChanged()
                .collect { account ->
                    _uiState.update {
                        it.copy(
                            account = account,
                            role = IdentityRole(accountUri = account.uri, baseUrl = baseUrl),
                        )
                    }
                }
        }
    }

}
