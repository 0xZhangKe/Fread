package com.zhangke.utopia.explore.screens.home.tab

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ExplorerFeedsContainerViewModel(
    private val statusProvider: StatusProvider,
    private val interactiveHandler: InteractiveHandler,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ContainerViewModel<ExplorerFeedsViewModel, ExplorerFeedsContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ExplorerFeedsViewModel {
        return ExplorerFeedsViewModel(
            type = params.type,
            statusProvider = statusProvider,
            interactiveHandler = interactiveHandler,
            buildStatusUiState = buildStatusUiState,
        )
    }

    fun getSubViewModel(type: ExplorerFeedsTabType): ExplorerFeedsViewModel {
        return obtainSubViewModel(Params(type))
    }

    class Params(val type: ExplorerFeedsTabType) : SubViewModelParams() {

        override val key: String
            get() = type.name
    }
}