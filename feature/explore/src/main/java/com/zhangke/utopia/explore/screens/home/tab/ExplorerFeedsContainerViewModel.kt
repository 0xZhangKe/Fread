package com.zhangke.utopia.explore.screens.home.tab

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.explore.usecase.GetExplorerItemUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExplorerFeedsContainerViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val getExplorerItem: GetExplorerItemUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ContainerViewModel<ExplorerFeedsViewModel, ExplorerFeedsContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ExplorerFeedsViewModel {
        return ExplorerFeedsViewModel(
            type = params.type,
            role = params.role,
            statusProvider = statusProvider,
            getExplorerItem = getExplorerItem,
            buildStatusUiState = buildStatusUiState,
        )
    }

    fun getSubViewModel(type: ExplorerFeedsTabType, role: IdentityRole): ExplorerFeedsViewModel {
        return obtainSubViewModel(Params(type, role))
    }

    class Params(
        val type: ExplorerFeedsTabType,
        val role: IdentityRole,
    ) : SubViewModelParams() {

        override val key: String
            get() = type.name + role
    }
}
