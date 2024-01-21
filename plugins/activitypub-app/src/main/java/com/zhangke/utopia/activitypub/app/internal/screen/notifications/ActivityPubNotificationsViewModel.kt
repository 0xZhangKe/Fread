package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights

class ActivityPubNotificationsViewModel(
    private val userUriInsights: UserUriInsights,
) : ContainerViewModel<ActivityPubNotificationsSubViewModel, ActivityPubNotificationsViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubNotificationsSubViewModel {
        return ActivityPubNotificationsSubViewModel(
            userUriInsights = userUriInsights,
        )
    }

    fun getSubViewModel(userUriInsights: UserUriInsights): ActivityPubNotificationsSubViewModel {
        val params = Params(userUriInsights)
        return obtainSubViewModel(params)
    }

    class Params(private val userUriInsights: UserUriInsights) : SubViewModelParams() {

        override val key: String
            get() = userUriInsights.toString()
    }
}
