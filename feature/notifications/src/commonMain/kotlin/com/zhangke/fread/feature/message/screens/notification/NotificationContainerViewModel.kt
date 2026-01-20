package com.zhangke.fread.feature.message.screens.notification

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.lifecycle.ContainerViewModel.SubViewModelParams
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.feature.message.repo.notification.NotificationsRepo
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount

class NotificationContainerViewModel(
    private val statusProvider: StatusProvider,
    private val notificationsRepo: NotificationsRepo,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val statusUpdater: StatusUpdater,
) : ContainerViewModel<NotificationViewModel, NotificationContainerViewModel.Params>() {

    fun getSubViewModel(
        account: LoggedAccount
    ): NotificationViewModel {
        return obtainSubViewModel(
            Params(account)
        )
    }

    override fun createSubViewModel(params: Params): NotificationViewModel {
        return NotificationViewModel(
            statusProvider = statusProvider,
            account = params.account,
            notificationsRepo = notificationsRepo,
            statusUiStateAdapter = statusUiStateAdapter,
            refactorToNewStatus = refactorToNewStatus,
            statusUpdater = statusUpdater,
        )
    }

    class Params(
        val account: LoggedAccount,
    ) : SubViewModelParams() {

        override val key: String
            get() = account.hashCode().toString()
    }
}
