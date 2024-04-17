package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.controller.LoadableController
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotification
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.NotificationsRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.VotePollUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.common.status.usecase.FormatStatusDisplayTimeUseCase
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

class ActivityPubNotificationsSubViewModel(
    private val userUriInsights: UserUriInsights,
    private val accountManager: ActivityPubAccountManager,
    private val statusInteractive: StatusInteractiveUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val formatStatusDisplayTime: FormatStatusDisplayTimeUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val notificationsRepo: NotificationsRepo,
    private val clientManager: ActivityPubClientManager,
    private val votePoll: VotePollUseCase,
) : SubViewModel() {

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
        )
    )

    private val _uiState = loadableController.mutableUiState

    val uiState = loadableController.uiState

    private val _snackMessage = MutableSharedFlow<TextString>()
    val snackMessage: SharedFlow<TextString> = _snackMessage.asSharedFlow()

    private var loggedAccount: ActivityPubLoggedAccount? = null

    init {
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

    fun onInteractive(
        statusNotification: NotificationUiState,
        uiInteraction: StatusUiInteraction,
    ) {
        val status = statusNotification.status ?: return
        val interaction = uiInteraction.statusInteraction ?: return
        launchInViewModel {
            statusInteractive(role, status.status, interaction)
                .map { activityPubStatusAdapter.toStatus(it, status.status.platform) }
                .map { buildStatusUiState(it) }
                .onSuccess { newStatus ->
                    updateStatus(statusNotification, newStatus)
                }.onFailure {
                    _snackMessage.emit(textOf(it.message.orEmpty()))
                }
        }
    }

    private suspend fun updateStatus(statusNotification: NotificationUiState, newStatus: StatusUiState) {
        _uiState.update { current ->
            current.copy(
                dataList = current.dataList.map {
                    if (it.status?.status?.id == newStatus.status.id) {
                        it.copy(status = newStatus)
                    } else {
                        it
                    }
                }
            )
        }
        statusNotification.toNotification()
            .copy(status = newStatus.status)
            .let { notificationsRepo.updateNotifications(it, userUriInsights.uri) }
    }

    fun onRejectClick(notification: NotificationUiState) {
        val accountId = notification.account.id
        val accountRepo = clientManager.getClient(role).accountRepo
        launchInViewModel {
            accountRepo.rejectFollowRequest(accountId)
                .onFailure {
                    _snackMessage.emit(textOf(it.message.orEmpty()))
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
                    _snackMessage.emit(textOf(it.message.orEmpty()))
                }.onSuccess {
                    onRefresh(true)
                }
        }
    }

    fun onVoted(statusNotification: NotificationUiState, options: List<BlogPoll.Option>) {
        val status = statusNotification.status?.status ?: return
        launchInViewModel {
            votePoll(role, status, options)
                .map { buildStatusUiState(it) }
                .onSuccess {
                    updateStatus(statusNotification, it)
                }.onFailure {
                    _snackMessage.emit(textOf(it.message.orEmpty()))
                }
        }
    }

    private fun StatusNotification.toUiState(): NotificationUiState {
        return NotificationUiState(
            id = id,
            type = type,
            createdAt = createdAt,
            account = account,
            accountUri = accountEntityAdapter.toUri(account),
            displayTime = formatStatusDisplayTime(createdAt.time),
            status = status?.let { buildStatusUiState(it) },
            relationshipSeveranceEvent = relationshipSeveranceEvent,
        )
    }

    private fun NotificationUiState.toNotification(): StatusNotification {
        return StatusNotification(
            id = id,
            type = type,
            createdAt = createdAt,
            account = account,
            status = status?.status,
            relationshipSeveranceEvent = relationshipSeveranceEvent,
        )
    }
}
