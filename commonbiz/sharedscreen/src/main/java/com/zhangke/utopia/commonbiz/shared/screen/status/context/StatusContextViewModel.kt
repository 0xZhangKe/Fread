package com.zhangke.utopia.commonbiz.shared.screen.status.context

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatusContextViewModel @Inject constructor(
    private val feedsRepo: FeedsRepo,
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : ContainerViewModel<StatusContextSubViewModel, StatusContextViewModel.Params>() {

    override fun createSubViewModel(params: Params): StatusContextSubViewModel {
        return StatusContextSubViewModel(
            feedsRepo = feedsRepo,
            statusProvider = statusProvider,
            buildStatusUiState = buildStatusUiState,
            refactorToNewBlog = refactorToNewBlog,
            baseUrl = params.baseUrl,
            anchorStatus = params.anchorStatus
        )
    }

    fun getSubViewModel(baseUrl: FormalBaseUrl, anchorStatus: Status): StatusContextSubViewModel {
        return obtainSubViewModel(Params(baseUrl, anchorStatus))
    }

    class Params(val baseUrl: FormalBaseUrl, val anchorStatus: Status) : SubViewModelParams() {

        override val key: String = anchorStatus.id + baseUrl
    }
}
