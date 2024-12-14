package com.zhangke.fread.bluesky.internal.screen.home

import app.bsky.feed.GetFeedQueryParams
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.Log
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.GetFeedsUseCase
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import sh.christian.ozone.api.AtUri

class BlueskyHomeViewModel(
    private val configId: Long,
    private val contentConfigRepo: ContentConfigRepo,
    private val clientManager: BlueskyClientManager,
    private val accountManager: BlueskyLoggedAccountManager,
    private val getFollowingFeeds: GetFeedsUseCase,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(BlueskyHomeUiState.default())
    val uiState: StateFlow<BlueskyHomeUiState> = _uiState

    private var observeAccountJob: Job? = null

    init {
        launchInViewModel {
            contentConfigRepo.getConfigFlowById(configId)
                .map { it as? ContentConfig.BlueskyContent }
                .collect { config ->
                    Log.i("F_TEST") { "config: $config" }
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
                    Log.i("F_TEST") { "account: $account" }
                    getBlueskyList()
                    _uiState.update {
                        it.copy(
                            account = account,
                            role = IdentityRole(accountUri = account.uri, baseUrl = baseUrl),
                        )
                    }
                }
        }
    }

    private suspend fun getBlueskyList() {
        getFollowingFeeds(_uiState.value.role)
            .onSuccess {
                Log.i("F_TEST") { "getFollowedFeeds: $it" }
            }.onFailure {
                Log.i("F_TEST") { "getFollowedFeeds error: $it" }
            }
        val atUri = AtUri("at://did:plc:gekdk2nd47gkk3utfz2xf7cn/app.bsky.feed.generator/aaap4tbjcfe5y")
        clientManager.getClient(_uiState.value.role)
            .getFeedCatching(GetFeedQueryParams(atUri))
            .onSuccess {
                Log.i("F_TEST") { "getFeedCatching: $it" }
            }.onFailure {
                Log.i("F_TEST") { "getFeedCatching error: $it" }
            }
//        clientManager.getClient(_uiState.value.role).getList()
    }
}
