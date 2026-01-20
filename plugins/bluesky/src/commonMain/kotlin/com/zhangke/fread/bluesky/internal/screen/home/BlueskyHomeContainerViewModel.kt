package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.lifecycle.ContainerViewModel.SubViewModelParams
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.usecase.UpdateHomeTabUseCase
import com.zhangke.fread.common.content.FreadContentRepo

class BlueskyHomeContainerViewModel(
    private val contentRepo: FreadContentRepo,
    private val accountManager: BlueskyLoggedAccountManager,
    private val updateHomeTab: UpdateHomeTabUseCase,
) : ContainerViewModel<BlueskyHomeViewModel, BlueskyHomeContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): BlueskyHomeViewModel {
        return BlueskyHomeViewModel(
            contentId = params.contentId,
            contentRepo = contentRepo,
            updateHomeTab = updateHomeTab,
            accountManager = accountManager,
        )
    }

    fun getSubViewModel(contentId: String): BlueskyHomeViewModel {
        return obtainSubViewModel(Params(contentId))
    }

    class Params(val contentId: String) : SubViewModelParams() {

        override val key: String get() = contentId.toString()
    }
}
