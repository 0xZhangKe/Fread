package com.zhangke.fread.common.notification

import com.zhangke.fread.common.di.ApplicationCoroutineScope
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class NotificationUnreadCounter(
    private val statusProvider: StatusProvider,
    private val applicationScope: ApplicationCoroutineScope,
) {

    private val _totalUnreadFlow = MutableStateFlow(0)
    val totalUnreadFlow: StateFlow<Int> get() = _totalUnreadFlow.asStateFlow()

    private val refreshMutex = Mutex()

    init {
        applicationScope.launch {
            statusProvider.accountManager.getAllAccountFlow().collect { accounts ->
                refreshInternal(accounts)
            }
        }
    }

    fun refresh() {
        applicationScope.launch {
            val accounts = statusProvider.accountManager.getAllLoggedAccount()
            refreshInternal(accounts)
        }
    }

    private suspend fun refreshInternal(accounts: List<LoggedAccount>) {
        refreshMutex.withLock {
            if (accounts.isEmpty()) {
                _totalUnreadFlow.value = 0
                return
            }
            val total = supervisorScope {
                accounts.map { account ->
                    async {
                        runCatching {
                            statusProvider.notificationResolver
                                .getUnreadNotificationsCount(account)
                                .getOrDefault(0)
                        }.getOrDefault(0)
                    }
                }.awaitAll().sum()
            }
            _totalUnreadFlow.value = total
        }
    }
}
