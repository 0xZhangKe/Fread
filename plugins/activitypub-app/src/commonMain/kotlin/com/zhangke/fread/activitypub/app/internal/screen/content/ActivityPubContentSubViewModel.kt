package com.zhangke.fread.activitypub.app.internal.screen.content

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.usecase.UpdateActivityPubUserListUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.content.GetUserCreatedListUseCase
import com.zhangke.fread.activitypub.app.internal.utils.createPlatformLocator
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.content.FreadContentRepo
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
    private val freadConfigManager: FreadConfigManager,
    private val updateActivityPubUserList: UpdateActivityPubUserListUseCase,
    val contentId: String,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(ActivityPubContentUiState.default())
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
                        val locator = createPlatformLocator(contentConfig)
                        _uiState.update {
                            it.copy(locator = locator, config = contentConfig)
                        }
                        startObserveAccount(contentConfig)
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
        launchInViewModel {
            freadConfigManager.homeTabRefreshButtonVisibleFlow
                .collect { visible ->
                    _uiState.update { it.copy(showRefreshButton = visible) }
                }
        }
        launchInViewModel {
            freadConfigManager.homeTabNextButtonVisibleFlow
                .collect { visible ->
                    _uiState.update { it.copy(showNextButton = visible) }
                }
        }
    }

    private fun startObserveAccount(content: ActivityPubContent) {
        observeAccountJob?.cancel()
        val accountUri = content.accountUri
        if (accountUri == null) {
            _uiState.update { it.copy(account = null) }
            return
        }
        observeAccountJob = launchInViewModel {
            accountManager.observeAccount(accountUri)
                .distinctUntilChanged()
                .collect { account ->
                    val accountCountInSamePlatform = if (account == null) {
                        0
                    } else {
                        accountManager.getAllLoggedAccount().count { it.baseUrl == account.baseUrl }
                    }
                    _uiState.update {
                        it.copy(
                            account = account,
                            showAccountInTopBar = accountCountInSamePlatform > 1
                        )
                    }
                    userCreatedListUpdated = false
                    updateUserCreateList()
                }
        }
    }

    private fun updateUserCreateList() {
        if (userCreatedListUpdated) return
        userCreatedListUpdated = true
        updateUserListJob?.cancel()
        val locator = _uiState.value.locator ?: return
        updateUserListJob = launchInViewModel {
            getUserCreatedList(locator)
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
