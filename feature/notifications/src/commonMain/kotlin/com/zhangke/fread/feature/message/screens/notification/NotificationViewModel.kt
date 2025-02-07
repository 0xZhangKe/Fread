package com.zhangke.fread.feature.message.screens.notification

import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.controller.LoadableController
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.feature.message.repo.notification.NotificationsRepo
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.StatusNotification
import kotlinx.coroutines.flow.update

class NotificationViewModel(
    private val statusProvider: StatusProvider,
    private val account: LoggedAccount,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val notificationsRepo: NotificationsRepo,
    statusUpdater: StatusUpdater,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val loadableController = LoadableController(
        coroutineScope = viewModelScope,
        initialUiState = NotificationUiState.default(account = account),
        onPostSnackMessage = {
            launchInViewModel {
                mutableErrorMessageFlow.emit(it)
            }
        }
    )

    private val _uiState = loadableController.mutableUiState

    val uiState = loadableController.uiState

    private var reportedNotificationId: String? = null
    private var cursor: String? = null

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { interactiveResult ->
                when (interactiveResult) {
                    is InteractiveHandleResult.UpdateStatus -> {
                        updateStatus(interactiveResult.status)
                    }

                    is InteractiveHandleResult.DeleteStatus -> {
                    }

                    is InteractiveHandleResult.UpdateFollowState -> {
                        // no-op
                    }
                }
            }
        )
        loadableController.initData(
            getDataFromServer = ::getDataFromServer,
            getDataFromLocal = ::getDataFromLocal,
        )
    }

    fun onSwitchTab(onlyMentions: Boolean) {
        _uiState.update { it.copy(inOnlyMentionTab = onlyMentions) }
        cursor = null
        loadableController.initData(
            getDataFromServer = ::getDataFromServer,
            getDataFromLocal = ::getDataFromLocal,
        )
    }

    fun onRefresh(hideRefreshing: Boolean = false) {
        loadableController.onRefresh(hideRefreshing) {
            getDataFromServer(null)
        }
    }

    fun onLoadMore() {
        loadableController.onLoadMore {
            getDataFromServer(loadMore = true)
        }
    }

    fun onNotificationShown(notification: StatusNotificationUiState) {
        if (!notification.unreadState) return
        _uiState.update { state ->
            state.copy(
                dataList = state.dataList.map {
                    if (it.id == notification.id) {
                        it.copy(unreadState = false)
                    } else {
                        it
                    }
                }
            )
        }
    }

    fun onPageResume() {
        val firstNotificationId = uiState.value
            .dataList
            .firstOrNull()
            ?.takeIf { !it.fromLocal }
            ?.id ?: return
        if (firstNotificationId == reportedNotificationId) return
        reportedNotificationId = firstNotificationId
        launchInViewModel {
            statusProvider.notificationResolver
                .updateUnreadNotification(
                    account = account,
                    notificationLastReadId = firstNotificationId
                )
        }
    }

    fun onRejectClick(notification: StatusNotification.FollowRequest) {
        launchInViewModel {
            statusProvider.notificationResolver
                .rejectFollowRequest(account, notification.author)
                .onSuccess { onRefresh(true) }
                .onFailure { mutableErrorMessageFlow.emitTextMessageFromThrowable(it) }
        }
    }

    fun onAcceptClick(notification: StatusNotification.FollowRequest) {
        launchInViewModel {
            statusProvider.notificationResolver
                .acceptFollowRequest(account, notification.author)
                .onSuccess { onRefresh(true) }
                .onFailure { mutableErrorMessageFlow.emitTextMessageFromThrowable(it) }
        }
    }

    private suspend fun getDataFromServer(
        cursor: String? = this.cursor,
        loadMore: Boolean = false,
    ): Result<List<StatusNotificationUiState>> {
        return statusProvider.notificationResolver.getNotifications(
            account = account,
            type = if (uiState.value.inOnlyMentionTab) {
                INotificationResolver.NotificationRequestType.MENTION
            } else {
                INotificationResolver.NotificationRequestType.ALL
            },
            cursor = cursor,
        ).map {
            this.cursor = it.cursor
            it.notifications.map { n -> StatusNotificationUiState(n, fromLocal = false) }
        }.onSuccess {
            if (loadMore || uiState.value.inOnlyMentionTab) {
                notificationsRepo.insertNotification(account.uri, it.map { n -> n.notification })
            } else {
                notificationsRepo.replaceNotifications(account.uri, it.map { n -> n.notification })
            }
        }
    }

    private suspend fun getDataFromLocal(): List<StatusNotificationUiState> {
        return notificationsRepo.getNotifications(account.uri)
            .filter {
                if (uiState.value.inOnlyMentionTab) {
                    it is StatusNotification.Mention || it is StatusNotification.Quote || it is StatusNotification.Reply
                } else {
                    true
                }
            }.sortedByDescending { it.createAt.epochMillis }
            .map { StatusNotificationUiState(it, fromLocal = true) }
    }

    private suspend fun updateStatus(newStatus: StatusUiState) {
        var updatedNotification: StatusNotification? = null
        _uiState.update { current ->
            current.copy(
                dataList = current.dataList.map { notification ->
                    notification.updateStatus(newStatus)?.let {
                        updatedNotification = it.notification
                        it
                    } ?: notification
                }
            )
        }

        if (updatedNotification != null) {
            notificationsRepo.updateNotification(
                accountUri = account.uri,
                notification = updatedNotification!!,
            )
        }
    }
}
