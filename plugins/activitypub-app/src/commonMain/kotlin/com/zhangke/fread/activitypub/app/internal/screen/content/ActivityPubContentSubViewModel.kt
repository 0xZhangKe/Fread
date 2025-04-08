package com.zhangke.fread.activitypub.app.internal.screen.content

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.usecase.UpdateActivityPubUserListUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.content.GetUserCreatedListUseCase
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class ActivityPubContentSubViewModel(
    private val contentRepo: FreadContentRepo,
    private val getUserCreatedList: GetUserCreatedListUseCase,
    private val accountManager: ActivityPubAccountManager,
    private val updateActivityPubUserList: UpdateActivityPubUserListUseCase,
    val contentId: String,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(ActivityPubContentUiState.DEFAULT)
    val uiState = _uiState.asStateFlow()

    private var updateUserListJob: Job? = null
    private var userCreatedListUpdated = false
    private var observeAccountJob: Job? = null

    init {
        launchInViewModel {
            contentRepo.getContentFlow(contentId)
                .distinctUntilChanged()
                .map { it as? ActivityPubContent }
                .collect { contentConfig ->
                    if (contentConfig != null) {
                        val role = IdentityRole(accountUri = null, baseUrl = contentConfig.baseUrl)
                        _uiState.update {
                            it.copy(role = role, config = contentConfig)
                        }
                        startObserveAccount(contentConfig.baseUrl)
                        updateUserCreateList()
                    } else {
                        _uiState.update {
                            it.copy(
                                errorMessage = "Cant find validate config by id: $contentId"
                            )
                        }
                    }
                }
        }
    }

    private fun startObserveAccount(baseUrl: FormalBaseUrl) {
        observeAccountJob?.cancel()
        observeAccountJob = launchInViewModel {
            accountManager.observeAccount(baseUrl)
                .distinctUntilChanged()
                .collect { account ->
                    _uiState.update { it.copy(account = account) }
                    userCreatedListUpdated = false
                    updateUserCreateList()
                }
        }
    }

    private fun updateUserCreateList() {
        if (userCreatedListUpdated) return
        if (_uiState.value.account == null) return
        userCreatedListUpdated = true
        updateUserListJob?.cancel()
        val role = _uiState.value.role ?: return
        updateUserListJob = launchInViewModel {
            getUserCreatedList(role)
                .map { list ->
                    list.map {
                        // 此处的 order 并不会使用，repo 内部会重新计算，因此次数放一个较大的值填充即可。
                        ActivityPubContent.ContentTab.ListTimeline(
                            listId = it.id,
                            name = it.title,
                            order = 1000,
                        )
                    }
                }
                .onSuccess { list ->
                    _uiState.value.config?.let { updateActivityPubUserList(it, list) }
                }
        }
    }
}
