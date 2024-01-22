package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivityPubNotificationsSubViewModel(
    private val userUriInsights: UserUriInsights,

): SubViewModel() {

    private val _uiState = MutableStateFlow(ActivityPubNotificationsUiState(
        notificationList = emptyList(),
        refreshing = false,
        loadMoreState = LoadState.Idle,
        errorMessage = null,
    ))
    val uiState = _uiState.asStateFlow()

    init {

    }
}
