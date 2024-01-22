package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotification

data class ActivityPubNotificationsUiState (
    val notificationList: List<StatusNotification>,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val errorMessage: TextString?,
)
