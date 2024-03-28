package com.zhangke.utopia.explore.screens.home.tab

import com.zhangke.framework.lifecycle.ContainerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ExplorerFeedsContainerViewModel :
    ContainerViewModel<ExplorerFeedsViewModel, ExplorerFeedsContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ExplorerFeedsViewModel {
        return ExplorerFeedsViewModel(type = params.type)
    }

    fun getSubViewModel(type: ExplorerFeedsTabType): ExplorerFeedsViewModel {
        return obtainSubViewModel(Params(type))
    }

    class Params(val type: ExplorerFeedsTabType) : SubViewModelParams() {

        override val key: String
            get() = type.name
    }
}