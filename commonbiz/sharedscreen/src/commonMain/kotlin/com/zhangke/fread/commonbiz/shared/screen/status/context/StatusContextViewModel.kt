package com.zhangke.fread.commonbiz.shared.screen.status.context

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.mixed.MixedStatusRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class StatusContextViewModel @Inject constructor(
    private val mixedStatusRepo: MixedStatusRepo,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
) : ContainerViewModel<StatusContextSubViewModel, StatusContextViewModel.Params>() {

    override fun createSubViewModel(params: Params): StatusContextSubViewModel {
        return StatusContextSubViewModel(
            mixedStatusRepo = mixedStatusRepo,
            statusProvider = statusProvider,
            statusUpdater = statusUpdater,
            statusUiStateAdapter = statusUiStateAdapter,
            refactorToNewStatus = refactorToNewStatus,
            locator = params.locator,
            anchorStatus = params.anchorStatus,
            blog = params.blog,
            blogId = params.blogId,
            blogTranslationUiState = params.blogTranslationUiState,
            platform = params.platform,
        )
    }

    fun getSubViewModel(
        locator: PlatformLocator,
        anchorStatus: StatusUiState?,
        blog: Blog?,
        blogId: String?,
        blogTranslationUiState: BlogTranslationUiState?,
        platform: BlogPlatform?,
    ): StatusContextSubViewModel {
        return obtainSubViewModel(
            Params(
                locator = locator,
                anchorStatus = anchorStatus,
                blog = blog,
                blogId = blogId,
                blogTranslationUiState = blogTranslationUiState,
                platform = platform,
            )
        )
    }

    class Params(
        val locator: PlatformLocator,
        val anchorStatus: StatusUiState?,
        val blog: Blog?,
        val blogId: String?,
        val blogTranslationUiState: BlogTranslationUiState?,
        val platform: BlogPlatform?,
    ) : SubViewModelParams() {

        override val key: String = anchorStatus?.status?.id + blog?.id + locator + blogId + platform
    }
}
