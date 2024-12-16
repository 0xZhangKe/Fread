package com.zhangke.fread.feeds.pages.home.feeds

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import me.tatarka.inject.annotations.Inject

class MixedContentViewModel @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val feedsRepo: FeedsRepo,
    private val statusUpdater: StatusUpdater,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : ContainerViewModel<MixedContentSubViewModel, MixedContentViewModel.Params>() {

    override fun createSubViewModel(params: Params): MixedContentSubViewModel {
        return MixedContentSubViewModel(
            contentRepo = contentRepo,
            feedsRepo = feedsRepo,
            statusUpdater = statusUpdater,
            buildStatusUiState = buildStatusUiState,
            statusProvider = statusProvider,
            configId = params.configId,
            refactorToNewBlog = refactorToNewBlog,
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
