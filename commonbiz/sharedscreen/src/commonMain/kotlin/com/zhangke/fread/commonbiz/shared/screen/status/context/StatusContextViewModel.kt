package com.zhangke.fread.commonbiz.shared.screen.status.context

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.ConvertNewBlogToStatusUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject

class StatusContextViewModel @Inject constructor(
    private val feedsRepo: FeedsRepo,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val convertNewBlogToStatus: ConvertNewBlogToStatusUseCase,
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
            blog = params.blog,
            blogTranslationUiState = params.blogTranslationUiState,
            convertNewBlogToStatus = convertNewBlogToStatus,
        )
    }

    fun getSubViewModel(
        role: IdentityRole,
        anchorStatus: Status?,
        blog: Blog?,
        blogTranslationUiState: BlogTranslationUiState?,
    ): StatusContextSubViewModel {
        return obtainSubViewModel(Params(role, anchorStatus, blog, blogTranslationUiState))
    }

    class Params(
        val role: IdentityRole,
        val anchorStatus: Status?,
        val blog: Blog?,
        val blogTranslationUiState: BlogTranslationUiState?,
    ) : SubViewModelParams() {

        override val key: String = anchorStatus?.id + blog?.id + role
    }
}
