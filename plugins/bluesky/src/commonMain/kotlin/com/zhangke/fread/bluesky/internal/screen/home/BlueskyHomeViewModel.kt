package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.usecase.UpdateHomeTabUseCase
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Job
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

    private val _uiState = MutableStateFlow(BlueskyHomeUiState.default())
    val uiState: StateFlow<BlueskyHomeUiState> = _uiState

    private var observeAccountJob: Job? = null

    init {
        launchInViewModel {
            contentRepo.getContentFlow(contentId)
                .map { it as? BlueskyContent }
                .collect { config ->
                    if (config != null) {
                        _uiState.update { state ->
                            state.copy(
                                role = IdentityRole(accountUri = null, baseUrl = config.baseUrl),
                                content = config,
                            )
                        }
                        startObserveAccount(config.baseUrl)
                    } else {
                        _uiState.update { state ->
                            state.copy(errorMessage = "Cant find validate config by id: $contentId")
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
                    getBlueskyList(account)
                    _uiState.update {
                        it.copy(
                            account = account,
                            role = IdentityRole(accountUri = account.uri, baseUrl = baseUrl),
                        )
                    }
                }
        }
    }

    private suspend fun getBlueskyList(account: BlueskyLoggedAccount) {
        updateHomeTab(contentId, _uiState.value.role, account.did)
    }
}
