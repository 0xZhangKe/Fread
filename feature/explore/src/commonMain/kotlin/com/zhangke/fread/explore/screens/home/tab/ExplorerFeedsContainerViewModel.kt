package com.zhangke.fread.explore.screens.home.tab

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.explore.usecase.GetExplorerItemUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class ExplorerFeedsContainerViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val getExplorerItem: GetExplorerItemUseCase,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
) : ContainerViewModel<ExplorerFeedsViewModel, ExplorerFeedsContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ExplorerFeedsViewModel {
        return ExplorerFeedsViewModel(
            type = params.type,
            role = params.role,
            platform = params.platform,
            statusUpdater = statusUpdater,
            statusProvider = statusProvider,
            getExplorerItem = getExplorerItem,
            refactorToNewStatus = refactorToNewStatus,
            statusUiStateAdapter = statusUiStateAdapter,
        )
    }

    fun getSubViewModel(
        type: ExplorerFeedsTabType,
        role: IdentityRole,
        platform: BlogPlatform,
    ): ExplorerFeedsViewModel {
        return obtainSubViewModel(Params(type, role, platform))
    }

    class Params(
        val type: ExplorerFeedsTabType,
        val role: IdentityRole,
        val platform: BlogPlatform,
    ) : SubViewModelParams() {

        override val key: String
            get() = type.name + role + platform
    }
}
