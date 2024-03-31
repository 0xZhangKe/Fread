package com.zhangke.utopia.explore.screens.home.tab

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.explore.usecase.GetExplorerItemUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ExplorerFeedsContainerViewModel(
    private val statusProvider: StatusProvider,
    private val interactiveHandler: InteractiveHandler,
    private val getExplorerItem: GetExplorerItemUseCase,
) : ContainerViewModel<ExplorerFeedsViewModel, ExplorerFeedsContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ExplorerFeedsViewModel {
        return ExplorerFeedsViewModel(
            type = params.type,
            accountUri = params.accountUri,
            statusProvider = statusProvider,
            interactiveHandler = interactiveHandler,
            getExplorerItem = getExplorerItem,
        )
    }

    fun getSubViewModel(type: ExplorerFeedsTabType, accountUri: FormalUri): ExplorerFeedsViewModel {
        return obtainSubViewModel(Params(type, accountUri))
    }

    class Params(
        val type: ExplorerFeedsTabType,
        val accountUri: FormalUri,
    ) : SubViewModelParams() {

        override val key: String
            get() = type.name
    }
}
