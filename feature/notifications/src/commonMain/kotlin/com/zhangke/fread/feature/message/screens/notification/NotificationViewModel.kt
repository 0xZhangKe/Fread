package com.zhangke.fread.feature.message.screens.notification

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

    fun switchTab(onlyMentions: Boolean) {
        _uiState.update { it.copy(onlyMentions = onlyMentions) }
        cursor = null
        loadableController.initData(
            getDataFromServer = ::getDataFromServer,
            getDataFromLocal = ::getDataFromLocal,
        )
    }

    fun onRefresh() {
        loadableController.onRefresh(false) {
            getDataFromServer(null)
        }
    }

    fun onLoadMore() {
        loadableController.onLoadMore {
            getDataFromServer(loadMore = true)
        }
    }

    private suspend fun getDataFromServer(
        cursor: String? = this.cursor,
        loadMore: Boolean = false,
    ): Result<List<StatusNotification>> {
        return statusProvider.notificationResolver.getNotifications(
            account = account,
            type = if (uiState.value.onlyMentions) {
                INotificationResolver.NotificationRequestType.MENTION
            } else {
                INotificationResolver.NotificationRequestType.ALL
            },
            cursor = cursor,
        ).map {
            this.cursor = it.cursor
            it.notifications
        }.onSuccess {
            if (loadMore || uiState.value.onlyMentions) {
                notificationsRepo.insertNotification(account.uri, it)
            } else {
                notificationsRepo.replaceNotifications(account.uri, it)
            }
        }
    }

    private suspend fun getDataFromLocal(): List<StatusNotification> {
        return notificationsRepo.getNotifications(account.uri)
            .filter {
                if (uiState.value.onlyMentions) {
                    it is StatusNotification.Mention || it is StatusNotification.Quote || it is StatusNotification.Reply
                } else {
                    true
                }
            }.sortedByDescending { it.createAt.epochMillis }
    }

    private suspend fun updateStatus(newStatus: StatusUiState) {
        var updatedNotification: StatusNotification? = null
        _uiState.update { current ->
            current.copy(
                dataList = current.dataList.map { notification ->
                    when (notification) {
                        is StatusNotification.Mention -> {
                            if (notification.status.status.id == newStatus.status.id) {
                                notification.copy(status = newStatus)
                                    .also { updatedNotification = it }
                            } else {
                                notification
                            }
                        }

                        is StatusNotification.Quote -> {
                            if (notification.quote.status.id == newStatus.status.id) {
                                notification.copy(quote = newStatus)
                                    .also { updatedNotification = it }
                            } else {
                                notification
                            }
                        }

                        is StatusNotification.Reply -> {
                            if (notification.reply.status.id == newStatus.status.id) {
                                notification.copy(reply = newStatus)
                                    .also { updatedNotification = it }
                            } else {
                                notification
                            }
                        }

                        else -> notification
                    }
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
