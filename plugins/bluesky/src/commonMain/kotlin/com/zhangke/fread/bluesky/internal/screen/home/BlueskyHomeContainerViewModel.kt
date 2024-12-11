package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.lifecycle.ContainerViewModel.SubViewModelParams
import com.zhangke.fread.bluesky.BlueskyAccountManager
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import me.tatarka.inject.annotations.Inject

class BlueskyHomeContainerViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val accountManager: BlueskyAccountManager,
) : ContainerViewModel<BlueskyHomeViewModel, BlueskyHomeContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): BlueskyHomeViewModel {
        return BlueskyHomeViewModel(
            configId = params.configId,
            contentConfigRepo = contentConfigRepo,
            accountManager = accountManager,
        )
    }

    fun getSubViewModel(configId: Long): BlueskyHomeViewModel {
        return obtainSubViewModel(Params(configId))
    }

    class Params(val configId: Long) : SubViewModelParams() {

        override val key: String get() = configId.toString()
    }
}
