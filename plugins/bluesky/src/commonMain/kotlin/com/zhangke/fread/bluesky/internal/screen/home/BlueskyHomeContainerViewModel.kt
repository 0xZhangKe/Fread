package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.lifecycle.ContainerViewModel.SubViewModelParams
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.GetFeedsUseCase
import com.zhangke.fread.common.content.FreadContentRepo
import me.tatarka.inject.annotations.Inject

class BlueskyHomeContainerViewModel @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val accountManager: BlueskyLoggedAccountManager,
    private val clientManager: BlueskyClientManager,
    private val getFollowingFeeds: GetFeedsUseCase,
) : ContainerViewModel<BlueskyHomeViewModel, BlueskyHomeContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): BlueskyHomeViewModel {
        return BlueskyHomeViewModel(
            contentId = params.contentId,
            contentRepo = contentRepo,
            clientManager = clientManager,
            accountManager = accountManager,
            getFollowingFeeds = getFollowingFeeds,
        )
    }

    fun getSubViewModel(contentId: String): BlueskyHomeViewModel {
        return obtainSubViewModel(Params(contentId))
    }

    class Params(val contentId: String) : SubViewModelParams() {

        override val key: String get() = contentId.toString()
    }
}
