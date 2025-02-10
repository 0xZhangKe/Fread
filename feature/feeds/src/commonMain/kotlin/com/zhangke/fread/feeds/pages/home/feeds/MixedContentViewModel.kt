package com.zhangke.fread.feeds.pages.home.feeds

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.mixed.MixedStatusRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import me.tatarka.inject.annotations.Inject

class MixedContentViewModel @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val mixedRepo: MixedStatusRepo,
    private val statusUpdater: StatusUpdater,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val statusProvider: StatusProvider,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
) : ContainerViewModel<MixedContentSubViewModel, MixedContentViewModel.Params>() {

    override fun createSubViewModel(params: Params): MixedContentSubViewModel {
        return MixedContentSubViewModel(
            contentRepo = contentRepo,
            mixedRepo = mixedRepo,
            statusUpdater = statusUpdater,
            statusUiStateAdapter = statusUiStateAdapter,
            statusProvider = statusProvider,
            configId = params.configId,
            refactorToNewStatus = refactorToNewStatus,
        )
    }

    fun getSubViewModel(configId: String): MixedContentSubViewModel {
        val params = Params(configId)
        return obtainSubViewModel(params)
    }

    class Params(val configId: String) : SubViewModelParams() {
        override val key: String
            get() = configId.toString()
    }
}
