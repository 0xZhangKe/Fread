package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.NotificationsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivityPubNotificationsSubViewModel(
    private val userUriInsights: UserUriInsights,
    private val accountManager: ActivityPubAccountManager,
    private val notificationsRepo: NotificationsRepo,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(
        ActivityPubNotificationsUiState(
            notificationList = emptyList(),
            refreshing = false,
            loadMoreState = LoadState.Idle,
            errorMessage = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel {
            val account = accountManager.getAllLoggedAccount()
                .firstOrNull { it.uri == userUriInsights.uri }
            if (account == null) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = textOf("Account not found: ${userUriInsights.uri}"),
                )
                return@launchInViewModel
            }

        }
    }
}
