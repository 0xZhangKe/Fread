package com.zhangke.fread.feature.message.screens.notification

import com.zhangke.framework.collections.getOrNull
import com.zhangke.framework.collections.removeFirstOrNull
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.controller.LoadableController
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.feature.message.repo.notification.NotificationsRepo
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.Relationships
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val statusProvider: StatusProvider,
    private val account: LoggedAccount,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val notificationsRepo: NotificationsRepo,
    statusUpdater: StatusUpdater,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
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
    private var reachEnd: Boolean = false

    private val userDetailSnapshot = mutableSetOf<BlogAuthor>()

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
                        updateUiStateRelationships(
                            authorUri = interactiveResult.userUri,
                            block = { relationships ->
                                relationships?.copy(
                                    following = interactiveResult.following,
                                )
                            }
                        )
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
        reachEnd = false
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
        if (reachEnd) return
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

    fun onRejectClick(author: BlogAuthor) {
        launchInViewModel {
            statusProvider.notificationResolver
                .rejectFollowRequest(account, author)
                .onSuccess { onRefresh(true) }
                .onFailure { mutableErrorMessageFlow.emitTextMessageFromThrowable(it) }
        }
    }

    fun onAcceptClick(author: BlogAuthor) {
        launchInViewModel {
            statusProvider.notificationResolver
                .acceptFollowRequest(account, author)
                .onSuccess { onRefresh(true) }
                .onFailure { mutableErrorMessageFlow.emitTextMessageFromThrowable(it) }
        }
    }

    fun onUnblockClick(locator: PlatformLocator, author: BlogAuthor) {
        launchInViewModel {
            statusProvider.accountManager.unblockAccount(
                account = account,
                user = author,
            ).onSuccess {
                updateUiStateRelationships(
                    authorUri = author.uri,
                    block = { relationships ->
                        relationships?.copy(blocking = false)
                    },
                )
            }.onFailure {
                mutableErrorMessageFlow.emitTextMessageFromThrowable(it)
            }
        }
    }

    fun onCancelFollowRequestClick(locator: PlatformLocator, author: BlogAuthor) {
        launchInViewModel {
            statusProvider.accountManager.cancelFollowRequest(
                account = account,
                user = author,
            ).onSuccess {
                updateUiStateRelationships(
                    authorUri = author.uri,
                    block = { relationships ->
                        relationships?.copy(requested = false)
                    },
                )
            }.onFailure {
                mutableErrorMessageFlow.emitTextMessageFromThrowable(it)
            }
        }
    }

    private fun updateUiStateRelationships(
        authorUri: FormalUri,
        block: (Relationships?) -> Relationships?,
    ) {
        userDetailSnapshot.removeFirstOrNull { it.uri == authorUri }?.let { user ->
            block(user.relationships)?.let {
                userDetailSnapshot.add(user.copy(relationships = it))
            }
        }
        _uiState.update { state ->
            state.copy(
                dataList = updateAuthorRelationships(
                    notifications = state.dataList,
                    authorUri = authorUri,
                    block = block,
                )
            )
        }
    }

    private fun updateAuthorRelationships(
        notifications: List<StatusNotificationUiState>,
        authorUri: FormalUri,
        block: (Relationships?) -> Relationships?,
    ): List<StatusNotificationUiState> {
        return notifications.map { notificationUiState ->
            val notification = notificationUiState.notification
            when (notification) {
                is StatusNotification.Follow -> {
                    if (notification.author.uri == authorUri) {
                        notificationUiState.copy(
                            notification = notification.copy(
                                author = notification.author.copy(
                                    relationships = block(notification.author.relationships)
                                )
                            )
                        )
                    } else {
                        notificationUiState
                    }
                }

                is StatusNotification.FollowRequest -> {
                    if (notification.author.uri == authorUri) {
                        notificationUiState.copy(
                            notification = notification.copy(
                                author = notification.author.copy(
                                    relationships = block(notification.author.relationships)
                                )
                            )
                        )
                    } else {
                        notificationUiState
                    }
                }

                else -> notificationUiState
            }
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
            this.reachEnd = it.reachEnd
            it.notifications
                .map { n -> StatusNotificationUiState(n, fromLocal = false) }
                .let { list -> fillUserDetails(list, userDetailSnapshot) }
        }.onSuccess {
            if (loadMore || uiState.value.inOnlyMentionTab) {
                notificationsRepo.insertNotification(
                    account.uri,
                    it.map { n -> n.notification })
            } else {
                notificationsRepo.replaceNotifications(
                    account.uri,
                    it.map { n -> n.notification })
            }
            launch { loadAdditionalData(account, it) }
        }
    }

    private suspend fun loadAdditionalData(
        account: LoggedAccount,
        notifications: List<StatusNotificationUiState>
    ) {
        // Just Follow/FollowRequest notifications need additional data currently.
        val users = notifications
            .mapNotNull {
                when (it.notification) {
                    is StatusNotification.Follow -> it.notification.author
                    is StatusNotification.FollowRequest -> it.notification.author
                    else -> null
                }
            }
            .distinctBy { it.uri }
        if (users.isEmpty()) return
        statusProvider.notificationResolver
            .getNotificationUserDetail(
                account = account,
                users = users,
            ).onSuccess { users ->
                if (users.isNotEmpty()) {
                    userDetailSnapshot.addAll(users)
                    _uiState.update { state ->
                        state.copy(
                            dataList = fillUserDetails(state.dataList, userDetailSnapshot)
                        )
                    }
                }
            }
    }

    private fun fillUserDetails(
        notifications: List<StatusNotificationUiState>,
        users: Collection<BlogAuthor>,
    ): List<StatusNotificationUiState> {
        if (users.isEmpty() || notifications.isEmpty()) return notifications
        return notifications.map { notificationUiState ->
            val notification = notificationUiState.notification
            when (notification) {
                is StatusNotification.Follow -> {
                    val user = users.getOrNull { it.uri == notification.author.uri }
                    if (user != null) {
                        notificationUiState.copy(notification = notification.copy(author = user))
                    } else {
                        notificationUiState
                    }
                }

                is StatusNotification.FollowRequest -> {
                    val user = users.getOrNull { it.uri == notification.author.uri }
                    if (user != null) {
                        notificationUiState.copy(notification = notification.copy(author = user))
                    } else {
                        notificationUiState
                    }
                }

                else -> notificationUiState
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
                notification = updatedNotification,
            )
        }
    }
}
