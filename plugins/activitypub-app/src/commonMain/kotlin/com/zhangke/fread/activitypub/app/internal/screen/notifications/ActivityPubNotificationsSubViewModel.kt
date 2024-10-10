package com.zhangke.fread.activitypub.app.internal.screen.notifications

import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.controller.LoadableController
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.model.StatusNotification
import com.zhangke.fread.activitypub.app.internal.model.UserUriInsights
import com.zhangke.fread.activitypub.app.internal.repo.NotificationsRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.common.status.usecase.FormatStatusDisplayTimeUseCase
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.update

class ActivityPubNotificationsSubViewModel(
    private val statusProvider: StatusProvider,
    private val userUriInsights: UserUriInsights,
    private val accountManager: ActivityPubAccountManager,
    statusUpdater: StatusUpdater,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val formatStatusDisplayTime: FormatStatusDisplayTimeUseCase,
    private val notificationsRepo: NotificationsRepo,
    private val clientManager: ActivityPubClientManager,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val role: IdentityRole
        get() = IdentityRole(userUriInsights.uri, null)

    private val loadableController = LoadableController(
        coroutineScope = viewModelScope,
        initialUiState = ActivityPubNotificationsUiState(
            role = role,
            inMentionsTab = false,
            dataList = emptyList(),
            initializing = false,
            refreshing = false,
            loadMoreState = LoadState.Idle,
            errorMessage = null,
        ),
        onPostSnackMessage = {
            launchInViewModel {
                mutableErrorMessageFlow.emit(it)
            }
        }
    )

    private val _uiState = loadableController.mutableUiState

    val uiState = loadableController.uiState

    private var loggedAccount: ActivityPubLoggedAccount? = null

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { interactiveResult ->
                when (interactiveResult) {
                    is InteractiveHandleResult.UpdateStatus -> {
                        updateStatus(interactiveResult.status)
                    }

                    is InteractiveHandleResult.DeleteStatus -> {
                        deleteStatus(interactiveResult.statusId)
                    }

                    is InteractiveHandleResult.UpdateFollowState -> {
                        // no-op
                    }
                }
            }
        )
        loadableController.initData(
            getDataFromServer = {
                getDataFromServer(_uiState.value.inMentionsTab)
            },
            getDataFromLocal = ::getDataFromLocal,
        )
    }

    fun onTabCheckedChange(inMentionsTab: Boolean) {
        _uiState.value = _uiState.value.copy(
            inMentionsTab = inMentionsTab,
        )
        loadableController.initData(
            getDataFromServer = {
                getDataFromServer(inMentionsTab)
            },
            getDataFromLocal = ::getDataFromLocal,
        )
    }

    fun onRefresh(hideRefreshing: Boolean = false) {
        loadableController.onRefresh(hideRefreshing) {
            getDataFromServer(_uiState.value.inMentionsTab)
        }
    }

    fun onLoadMore() {
        val latestId = _uiState.value.dataList.lastOrNull()?.id ?: return
        loadableController.onLoadMore {
            loadMoreDataFromServer(
                onlyMentions = _uiState.value.inMentionsTab,
                maxId = latestId,
            )
        }
    }

    private suspend fun getDataFromServer(
        onlyMentions: Boolean,
    ): Result<List<NotificationUiState>> {
        val account = getLoggedAccount() ?: return Result.failure(
            IllegalStateException("Account not found: ${userUriInsights.uri}")
        )
        return notificationsRepo.getRemoteNotifications(
            account = account,
            onlyMentions = onlyMentions,
        ).map { it.map { notification -> notification.toUiState() } }
    }

    private suspend fun loadMoreDataFromServer(
        onlyMentions: Boolean,
        maxId: String,
    ): Result<List<NotificationUiState>> {
        val account = getLoggedAccount() ?: return Result.failure(
            IllegalStateException("Account not found: ${userUriInsights.uri}")
        )
        return notificationsRepo.loadMoreNotifications(
            account = account,
            maxId = maxId,
            onlyMentions = onlyMentions,
        ).map {
            it.map { notification -> notification.toUiState() }
        }
    }

    private suspend fun getDataFromLocal(): List<NotificationUiState> {
        val account = getLoggedAccount() ?: return emptyList()
        return notificationsRepo.getLocalNotifications(
            accountOwnershipUri = account.uri,
            onlyMentions = _uiState.value.inMentionsTab,
        ).map { it.toUiState() }
    }

    private suspend fun getLoggedAccount(): ActivityPubLoggedAccount? {
        loggedAccount?.let { return it }
        val account = accountManager.getAllLoggedAccount()
            .firstOrNull { it.uri == userUriInsights.uri }
        loggedAccount = account
        return account
    }

    private suspend fun updateStatus(newStatus: StatusUiState) {
        _uiState.update { current ->
            current.copy(
                dataList = current.dataList.map {
                    if (it.status?.status?.intrinsicBlog?.id == newStatus.status.intrinsicBlog.id) {
                        it.copy(status = newStatus)
                    } else {
                        it
                    }
                }
            )
        }

        notificationsRepo.updateNotificationStatus(
            accountOwnershipUri = userUriInsights.uri,
            status = newStatus.status
        )
    }

    private fun deleteStatus(statusId: String) {
        _uiState.update { current ->
            current.copy(
                dataList = current.dataList.filter {
                    it.status?.status?.id != statusId
                }
            )
        }
    }

    fun onRejectClick(notification: NotificationUiState) {
        val accountId = notification.account.id
        val accountRepo = clientManager.getClient(role).accountRepo
        launchInViewModel {
            accountRepo.rejectFollowRequest(accountId)
                .onFailure {
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(it)
                }.onSuccess {
                    onRefresh(true)
                }
        }
    }

    fun onAcceptClick(notification: NotificationUiState) {
        val accountId = notification.account.id
        val accountRepo = clientManager.getClient(role).accountRepo
        launchInViewModel {
            accountRepo.authorizeFollowRequest(accountId)
                .onFailure {
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(it)
                }.onSuccess {
                    onRefresh(true)
                }
        }
    }

    private suspend fun StatusNotification.toUiState(): NotificationUiState {
        return NotificationUiState(
            id = id,
            role = role,
            type = type,
            createdAt = createdAt,
            account = account,
            author = accountEntityAdapter.toAuthor(account),
            displayTime = formatStatusDisplayTime(createdAt.toEpochMilliseconds()),
            status = status?.let { buildStatusUiState(role, it) },
            relationshipSeveranceEvent = relationshipSeveranceEvent,
        )
    }
}
