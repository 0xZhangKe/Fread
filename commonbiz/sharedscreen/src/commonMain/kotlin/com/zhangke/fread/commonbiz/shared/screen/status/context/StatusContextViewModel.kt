package com.zhangke.fread.commonbiz.shared.screen.status.context

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.mixed.MixedStatusRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
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
            role = params.role,
            anchorStatus = params.anchorStatus,
            blog = params.blog,
            blogTranslationUiState = params.blogTranslationUiState,
        )
    }

    fun getSubViewModel(
        role: IdentityRole,
        anchorStatus: StatusUiState?,
        blog: Blog?,
        blogTranslationUiState: BlogTranslationUiState?,
    ): StatusContextSubViewModel {
        return obtainSubViewModel(Params(role, anchorStatus, blog, blogTranslationUiState))
    }

    class Params(
        val role: IdentityRole,
        val anchorStatus: StatusUiState?,
        val blog: Blog?,
        val blogTranslationUiState: BlogTranslationUiState?,
    ) : SubViewModelParams() {

        override val key: String = anchorStatus?.status?.id + blog?.id + role
    }
}
