package com.zhangke.fread.feeds.pages.home.feeds

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import me.tatarka.inject.annotations.Inject

class MixedContentViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val feedsRepo: FeedsRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : ContainerViewModel<MixedContentSubViewModel, MixedContentViewModel.Params>() {

    override fun createSubViewModel(params: Params): MixedContentSubViewModel {
        return MixedContentSubViewModel(
            contentConfigRepo = contentConfigRepo,
            feedsRepo = feedsRepo,
            buildStatusUiState = buildStatusUiState,
            statusProvider = statusProvider,
            configId = params.configId,
            refactorToNewBlog = refactorToNewBlog,
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
