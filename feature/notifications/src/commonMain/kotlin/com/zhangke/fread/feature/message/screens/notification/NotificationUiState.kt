package com.zhangke.fread.feature.message.screens.notification

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.LoadableUiState
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.notification.StatusNotification

data class NotificationUiState(
    val account: LoggedAccount,
    val onlyMentions: Boolean,
    override val initializing: Boolean,
    override val dataList: List<StatusNotification>,
    override val refreshing: Boolean,
    override val loadMoreState: LoadState,
    override val errorMessage: TextString?,
) : LoadableUiState<StatusNotification, NotificationUiState> {

    override fun copyObject(
        dataList: List<StatusNotification>,
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
                onlyMentions = false,
                initializing = false,
                dataList = emptyList(),
                refreshing = false,
                loadMoreState = LoadState.Idle,
                errorMessage = null,
            )
        }
    }
}
