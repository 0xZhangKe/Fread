package com.zhangke.utopia.activitypub.app.internal.screen.content

import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.successDataOrNull
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.composable.updateToLoading
import com.zhangke.framework.composable.updateToSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.usecase.content.GetUserCreatedListUseCase
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.IdentityRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class ActivityPubContentSubViewModel(
    private val contentConfigRepo: ContentConfigRepo,
    private val getUserCreatedList: GetUserCreatedListUseCase,
    val configId: Long,
) : SubViewModel() {

    private val _uiState =
        MutableStateFlow<LoadableState<ActivityPubContentUiState>>(LoadableState.idle())
    val uiState = _uiState.asStateFlow()

    private var updateUserListJob: Job? = null
    private var userCreatedListUpdated = false

    init {
        launchInViewModel {
            _uiState.updateToLoading()
            contentConfigRepo.getConfigFlowById(configId)
                .map { it as? ContentConfig.ActivityPubContent }
                .collect { contentConfig ->
                    if (contentConfig != null) {
                        val role = IdentityRole(accountUri = null, baseUrl = contentConfig.baseUrl)
                        _uiState.updateToSuccess(ActivityPubContentUiState(role, contentConfig))
                        updateUserCreateList()
                    } else {
                        _uiState.updateToFailed(IllegalArgumentException("Cant find validate config by id: $configId"))
                    }
                }
        }
    }

    private fun updateUserCreateList() {
        if (userCreatedListUpdated) return
        userCreatedListUpdated = true
        if (updateUserListJob?.isActive == true) {
            updateUserListJob?.cancel()
        }
        val role = _uiState.value.successDataOrNull()?.role ?: return
        updateUserListJob = launchInViewModel {
            getUserCreatedList(role)
                .map { list ->
                    list.map {
                        // 此处的 order 并不会使用，repo 内部会重新计算，因此次数放一个较大的值填充即可。
                        ContentConfig.ActivityPubContent.ContentTab.ListTimeline(
                            listId = it.id,
                            name = it.title,
                            order = 1000,
                        )
                    }
                }
                .onSuccess {
                    contentConfigRepo.updateActivityPubUserList(configId, it)
                }
        }
    }
}
