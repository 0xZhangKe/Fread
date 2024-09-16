package com.zhangke.fread.commonbiz.shared.screen.status.context

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatusContextViewModel @Inject constructor(
    private val feedsRepo: FeedsRepo,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : ContainerViewModel<StatusContextSubViewModel, StatusContextViewModel.Params>() {

    override fun createSubViewModel(params: Params): StatusContextSubViewModel {
        return StatusContextSubViewModel(
            feedsRepo = feedsRepo,
            statusProvider = statusProvider,
            statusUpdater = statusUpdater,
            buildStatusUiState = buildStatusUiState,
            refactorToNewBlog = refactorToNewBlog,
            role = params.role,
            anchorStatus = params.anchorStatus,
            blogTranslationUiState = params.blogTranslationUiState,
        )
    }

    fun getSubViewModel(
        role: IdentityRole,
        anchorStatus: Status,
        blogTranslationUiState: BlogTranslationUiState?,
    ): StatusContextSubViewModel {
        return obtainSubViewModel(Params(role, anchorStatus, blogTranslationUiState))
    }

    class Params(
        val role: IdentityRole,
        val anchorStatus: Status,
        val blogTranslationUiState: BlogTranslationUiState?,
    ) : SubViewModelParams() {

        override val key: String = anchorStatus.id + role
    }
}
