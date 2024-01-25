package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotification
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.NotificationsRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.common.status.usecase.FormatStatusDisplayTimeUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ActivityPubNotificationsSubViewModel(
    private val userUriInsights: UserUriInsights,
    private val accountManager: ActivityPubAccountManager,
    private val statusInteractive: StatusInteractiveUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val formatStatusDisplayTime: FormatStatusDisplayTimeUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val notificationsRepo: NotificationsRepo,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(
        ActivityPubNotificationsUiState(
            notificationList = emptyList(),
            inMentionsTab = false,
            refreshing = false,
            loadMoreState = LoadState.Idle,
            errorMessage = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _snackMessage = MutableSharedFlow<TextString>()
    val snackMessage: SharedFlow<TextString> = _snackMessage.asSharedFlow()

    private var loggedAccount: ActivityPubLoggedAccount? = null

    init {
        launchInViewModel {
            var account = loggedAccount
            account = accountManager.getAllLoggedAccount()
                .firstOrNull { it.uri == userUriInsights.uri }
            loggedAccount = account
            if (account == null) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = textOf("Account not found: ${userUriInsights.uri}"),
                )
                return@launchInViewModel
            }
            notificationsRepo.getLocalNotifications(account.uri)
                .takeIf { it.isNotEmpty() }
                ?.map { it.toUiState() }
                ?.let { _uiState.value = _uiState.value.copy(notificationList = it) }
            refresh(false)
        }
    }

    fun onTabCheckedChange(inMentionsTab: Boolean) {
        _uiState.value = _uiState.value.copy(
            inMentionsTab = inMentionsTab,
        )
    }

    fun onRefresh() {
        launchInViewModel {
            refresh(true)
        }
    }

    private suspend fun refresh(showRefreshing: Boolean) {
        if (_uiState.value.refreshing || _uiState.value.loadMoreState == LoadState.Loading) return
        val account = loggedAccount ?: return
        updateRefreshState(showRefreshing, true, null)
        notificationsRepo.getRemoteNotifications(account)
            .map {
                it.map { notification -> notification.toUiState() }
            }
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    notificationList = it,
                    refreshing = false,
                    errorMessage = null,
                )
            }.onFailure {
                val errorMessage = textOf(it.message.orEmpty())
                updateRefreshState(showRefreshing, false, errorMessage)
                _snackMessage.emit(errorMessage)
            }
    }

    fun onLoadMore() {
        val account = loggedAccount ?: return
        if (_uiState.value.refreshing || _uiState.value.loadMoreState == LoadState.Loading) return
        val latestNotification = _uiState.value.notificationList.lastOrNull() ?: return
        launchInViewModel {
            _uiState.value = _uiState.value.copy(
                loadMoreState = LoadState.Loading,
            )
            notificationsRepo.loadMoreNotifications(
                account = account,
                maxId = latestNotification.id,
            ).map {
                it.map { notification -> notification.toUiState() }
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    notificationList = _uiState.value.notificationList + it,
                    loadMoreState = LoadState.Idle,
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    loadMoreState = LoadState.Failed(it),
                )
            }
        }
    }

    fun onInteractive(
        statusNotification: NotificationUiState,
        uiInteraction: StatusUiInteraction,
    ) {
        val status = statusNotification.status ?: return
        val interaction = uiInteraction.statusInteraction ?: return
        launchInViewModel {
            statusInteractive(status.status, interaction)
                .map { activityPubStatusAdapter.toStatus(it, status.status.platform) }
                .map { buildStatusUiState(it) }
                .onSuccess { newStatus ->
                    _uiState.update { current ->
                        current.copy(
                            notificationList = current.notificationList.map {
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
                        .let { notificationsRepo.updateNotifications(it) }
                }.onFailure {
                    _snackMessage.emit(textOf(it.message.orEmpty()))
                }
        }
    }

    private fun updateRefreshState(
        showRefreshing: Boolean,
        refreshing: Boolean,
        errorMessage: TextString? = _uiState.value.errorMessage,
    ) {
        _uiState.value = _uiState.value.copy(
            refreshing = showRefreshing && refreshing,
            errorMessage = errorMessage,
        )
    }

    private fun StatusNotification.toUiState(): NotificationUiState {
        return NotificationUiState(
            id = id,
            type = type,
            createdAt = createdAt,
            account = account,
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
