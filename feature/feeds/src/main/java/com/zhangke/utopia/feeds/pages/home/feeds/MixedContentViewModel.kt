package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MixedContentViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val feedsRepo: FeedsRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
) : ContainerViewModel<MixedContentSubViewModel, MixedContentViewModel.Params>() {

    override fun createSubViewModel(params: Params): MixedContentSubViewModel {
        return MixedContentSubViewModel(
            contentConfigRepo = contentConfigRepo,
            feedsRepo = feedsRepo,
            buildStatusUiState = buildStatusUiState,
            statusProvider = statusProvider,
            configId = params.configId,
        )
    }

    fun getSubViewModel(configId: Long): MixedContentSubViewModel {
        val params = Params(configId)
        return obtainSubViewModel(params)
    }

    class Params(val configId: Long) : SubViewModelParams() {
        override val key: String
            get() = configId.toString()
    }
}
