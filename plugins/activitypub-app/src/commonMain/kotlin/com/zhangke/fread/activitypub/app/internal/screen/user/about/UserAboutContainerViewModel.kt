package com.zhangke.fread.activitypub.app.internal.screen.user.about

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class UserAboutContainerViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) : ContainerViewModel<UserAboutViewModel, UserAboutContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): UserAboutViewModel {
        return UserAboutViewModel(
            clientManager = clientManager,
            webFingerBaseUrlToUserIdRepo = webFingerBaseUrlToUserIdRepo,
            emojiEntityAdapter = emojiEntityAdapter,
            role = params.role,
            webFinger = params.webFinger,
            userId = params.userId,
        )
    }

    fun getViewModel(
        role: IdentityRole,
        webFinger: WebFinger,
        userId: String?,
    ): UserAboutViewModel {
        return obtainSubViewModel(
            Params(role, webFinger, userId)
        )
    }

    class Params(
        val role: IdentityRole,
        val webFinger: WebFinger,
        val userId: String?,
    ) : SubViewModelParams() {

        override val key: String
            get() = role.toString() + webFinger + userId
    }
}