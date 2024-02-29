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
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.usecase.content.GetUserCreatedListUseCase
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.model.ContentConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivityPubContentSubViewModel(
    private val contentConfigRepo: ContentConfigRepo,
    private val accountManager: ActivityPubAccountManager,
    private val clientManager: ActivityPubClientManager,
    private val getUserCreatedList: GetUserCreatedListUseCase,
    val configId: Long,
) : SubViewModel() {

    private val _uiState =
        MutableStateFlow<LoadableState<ActivityPubContentUiState>>(LoadableState.idle())
    val uiState = _uiState.asStateFlow()

    private val _lists = MutableStateFlow<List<ActivityPubListEntity>>(emptyList())
    val lists: StateFlow<List<ActivityPubListEntity>> = _lists

    private var updateUserListJob: Job? = null

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
                updateUserList(contentConfig.baseUrl)
            } else {
                _uiState.updateToFailed(IllegalArgumentException("Cant find validate config by id: $configId"))
            }
        }
    }

    private fun updateUserList(baseUrl: FormalBaseUrl) {
        if (updateUserListJob?.isActive == true) updateUserListJob?.cancel()
        updateUserListJob = launchInViewModel {
            getUserCreatedList(baseUrl)
                .map { list ->
                    list.map {
                        ContentConfig.ActivityPubContent.ContentTab.ListTimeline(
                            listId = it.id,
                            name = it.title,
                        )
                    }
                }
                .onSuccess {
                    contentConfigRepo.updateActivityPubUserList(configId, it)
                }
        }
    }
}
