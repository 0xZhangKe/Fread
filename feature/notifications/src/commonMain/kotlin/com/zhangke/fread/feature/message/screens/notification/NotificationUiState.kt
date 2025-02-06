package com.zhangke.fread.feature.message.screens.notification

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.LoadableUiState
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.notification.StatusNotification

data class NotificationUiState(
    val account: LoggedAccount,
    val inOnlyMentionTab: Boolean,
    override val initializing: Boolean,
    override val dataList: List<StatusNotificationUiState>,
    override val refreshing: Boolean,
    override val loadMoreState: LoadState,
    override val errorMessage: TextString?,
) : LoadableUiState<StatusNotificationUiState, NotificationUiState> {

    override fun copyObject(
        dataList: List<StatusNotificationUiState>,
        initializing: Boolean,
        refreshing: Boolean,
        loadMoreState: LoadState,
        errorMessage: TextString?
    ): NotificationUiState {
        return copy(
            dataList = dataList,
            initializing = initializing,
            refreshing = refreshing,
            loadMoreState = loadMoreState,
            errorMessage = errorMessage,
        )
    }

    companion object {

        fun default(
            account: LoggedAccount,
        ): NotificationUiState {
            return NotificationUiState(
                account = account,
                inOnlyMentionTab = false,
                initializing = false,
                dataList = emptyList(),
                refreshing = false,
                loadMoreState = LoadState.Idle,
                errorMessage = null,
            )
        }
    }
}

data class StatusNotificationUiState(
    val notification: StatusNotification,
    val unreadState: Boolean = notification.unread,
) {

    val id: String get() = notification.id

    fun updateStatus(status: StatusUiState): StatusNotificationUiState? {
        if (notification.status?.status?.id != status.status.id) return null
        return when (notification) {
            is StatusNotification.Mention -> copy(notification = notification.copy(status = status))
            is StatusNotification.Reply -> copy(notification = notification.copy(reply = status))
            is StatusNotification.Quote -> copy(notification = notification.copy(quote = status))
            else -> null
        }
    }
}
