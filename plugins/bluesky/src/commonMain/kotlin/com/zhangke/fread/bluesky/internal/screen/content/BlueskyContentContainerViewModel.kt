package com.zhangke.fread.bluesky.internal.screen.content

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.UpdateHomeTabUseCase
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.content.FreadContentRepo

class BlueskyContentContainerViewModel(
    private val contentRepo: FreadContentRepo,
    private val freadConfigManager: FreadConfigManager,
    private val accountManager: BlueskyLoggedAccountManager,
    private val updateHomeTab: UpdateHomeTabUseCase,
    private val clientManager: BlueskyClientManager,
) : ContainerViewModel<BlueskyContentViewModel, BlueskyContentContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): BlueskyContentViewModel {
        return BlueskyContentViewModel(
            contentId = params.contentId,
            contentRepo = contentRepo,
            updateHomeTab = updateHomeTab,
            freadConfigManager = freadConfigManager,
            accountManager = accountManager,
            clientManager = clientManager,
        )
    }

    fun getSubViewModel(contentId: String): BlueskyContentViewModel {
        return obtainSubViewModel(Params(contentId))
    }

    class Params(val contentId: String) : SubViewModelParams() {

        override val key: String get() = contentId.toString()
    }
}
