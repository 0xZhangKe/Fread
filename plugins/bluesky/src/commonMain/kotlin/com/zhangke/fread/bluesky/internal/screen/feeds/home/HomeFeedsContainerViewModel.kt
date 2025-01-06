package com.zhangke.fread.bluesky.internal.screen.feeds.home

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class HomeFeedsContainerViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
) : ContainerViewModel<HomeFeedsViewModel, HomeFeedsContainerViewModel.Params>() {

    fun getViewModel(
        feeds: BlueskyFeeds,
        role: IdentityRole,
    ): HomeFeedsViewModel {
        return obtainSubViewModel(
            Params(feeds = feeds, role = role)
        )
    }

    override fun createSubViewModel(params: Params): HomeFeedsViewModel {
        return HomeFeedsViewModel(
            clientManager = clientManager,
            feeds = params.feeds,
            role = params.role,
        )
    }

    class Params(
        val feeds: BlueskyFeeds,
        val role: IdentityRole,
    ) : SubViewModelParams() {

        override val key: String
            get() = feeds.toString() + role
    }
}
