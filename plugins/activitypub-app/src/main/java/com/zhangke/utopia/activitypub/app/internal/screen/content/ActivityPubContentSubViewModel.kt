package com.zhangke.utopia.activitypub.app.internal.screen.content

import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.composable.updateToLoading
import com.zhangke.framework.composable.updateToSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.repo.account.AccountListsRepo
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.model.ContentConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ActivityPubContentSubViewModel(
    private val contentConfigRepo: ContentConfigRepo,
    private val accountManager: ActivityPubAccountManager,
    private val accountListsRepo: AccountListsRepo,
    val configId: Long,
) : SubViewModel() {

    private val _uiState =
        MutableStateFlow<LoadableState<ActivityPubContentUiState>>(LoadableState.idle())
    val uiState = _uiState.asStateFlow()

    private val _lists = MutableStateFlow<List<ActivityPubListEntity>>(emptyList())
    val lists: StateFlow<List<ActivityPubListEntity>> = _lists

    private var observeAccountJob: Job? = null

    init {
        loadContentConfig()
    }

    private fun loadContentConfig() {
        if (_uiState.value.isLoading) return
        launchInViewModel {
            _uiState.updateToLoading()
            val contentConfig =
                contentConfigRepo.getConfigById(configId) as? ContentConfig.ActivityPubContent
            if (contentConfig != null) {
                _uiState.updateToSuccess(ActivityPubContentUiState(contentConfig))
                observeAccountChanged(contentConfig.baseUrl)
            } else {
                _uiState.updateToFailed(IllegalArgumentException("Cant find validate config by id: $configId"))
            }
        }
    }

    private fun observeAccountChanged(baseUrl: FormalBaseUrl) {
        if (observeAccountJob?.isActive == true) observeAccountJob?.cancel()
        observeAccountJob = launchInViewModel {
            accountManager.getAllAccountFlow()
                .map { it.firstOrNull { account -> account.baseUrl == baseUrl } }
                .filterNotNull()
                .onEach(accountListsRepo::updateAccountLists)
                .flatMapMerge { account ->
                    accountListsRepo.observeAccountLists(account.userId)
                }.collect { lists ->
                    _lists.value = lists
                }
        }
    }
}
