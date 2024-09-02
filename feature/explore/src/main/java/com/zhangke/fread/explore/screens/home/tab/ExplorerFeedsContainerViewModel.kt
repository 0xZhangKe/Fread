package com.zhangke.fread.explore.screens.home.tab

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.explore.usecase.GetExplorerItemUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExplorerFeedsContainerViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val getExplorerItem: GetExplorerItemUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : ContainerViewModel<ExplorerFeedsViewModel, ExplorerFeedsContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ExplorerFeedsViewModel {
        return ExplorerFeedsViewModel(
            type = params.type,
            role = params.role,
            statusUpdater = statusUpdater,
            statusProvider = statusProvider,
            getExplorerItem = getExplorerItem,
            refactorToNewBlog = refactorToNewBlog,
            buildStatusUiState = buildStatusUiState,
        )
    }

    fun getSubViewModel(type: ExplorerFeedsTabType, role: IdentityRole): ExplorerFeedsViewModel {
        return obtainSubViewModel(Params(type, role))
    }

    class Params(
        val type: ExplorerFeedsTabType,
        val role: IdentityRole,
    ) : SubViewModelParams() {

        override val key: String
            get() = type.name + role
    }
}
