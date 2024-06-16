package com.zhangke.fread.activitypub.app.internal.screen.user.about

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.UserUriInsights
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.fread.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserAboutContainerViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) : ContainerViewModel<UserAboutViewModel, UserAboutContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): UserAboutViewModel {
        return UserAboutViewModel(
            clientManager = clientManager,
            formatDatetimeToDate = formatDatetimeToDate,
            webFingerBaseUrlToUserIdRepo = webFingerBaseUrlToUserIdRepo,
            emojiEntityAdapter = emojiEntityAdapter,
            role = params.role,
            webFinger = params.webFinger,
        )
    }

    fun getViewModel(
        role: IdentityRole,
        webFinger: WebFinger,
    ): UserAboutViewModel {
        return obtainSubViewModel(
            Params(role, webFinger)
        )
    }

    class Params(
        val role: IdentityRole,
        val webFinger: WebFinger,
    ) : SubViewModelParams() {

        override val key: String
            get() = role.toString() + webFinger
    }
}