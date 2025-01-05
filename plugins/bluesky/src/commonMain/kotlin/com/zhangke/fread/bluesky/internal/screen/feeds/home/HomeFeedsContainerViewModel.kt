package com.zhangke.fread.bluesky.internal.screen.feeds.home

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class HomeFeedsContainerViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
) : ContainerViewModel<HomeFeedsViewModel, HomeFeedsContainerViewModel.Params>() {

    fun getViewModel(
        tab: BlueskyContent.BlueskyTab,
        role: IdentityRole,
    ): HomeFeedsViewModel {
        return obtainSubViewModel(
            Params(tab = tab, role = role)
        )
    }

    override fun createSubViewModel(params: Params): HomeFeedsViewModel {
        return HomeFeedsViewModel(
            clientManager = clientManager,
            tab = params.tab,
            role = params.role,
        )
    }

    class Params(
        val tab: BlueskyContent.BlueskyTab,
        val role: IdentityRole,
    ) : SubViewModelParams() {

        override val key: String
            get() = tab.toString() + role
    }
}
