package com.zhangke.utopia.activitypub.app.internal.screen.user.about

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.emoji.MapAccountEntityEmojiUseCase
import com.zhangke.utopia.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserAboutContainerViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val mapAccountEntityEmoji: MapAccountEntityEmojiUseCase,
) : ContainerViewModel<UserAboutViewModel, UserAboutContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): UserAboutViewModel {
        return UserAboutViewModel(
            clientManager = clientManager,
            formatDatetimeToDate = formatDatetimeToDate,
            webFingerBaseUrlToUserIdRepo = webFingerBaseUrlToUserIdRepo,
            mapAccountEntityEmoji = mapAccountEntityEmoji,
            role = params.role,
            userUriInsights = params.userUriInsights,
        )
    }

    fun getViewModel(
        role: IdentityRole,
        userUriInsights: UserUriInsights
    ): UserAboutViewModel {
        return obtainSubViewModel(
            Params(role, userUriInsights)
        )
    }

    class Params(
        val role: IdentityRole,
        val userUriInsights: UserUriInsights,
    ) : SubViewModelParams() {

        override val key: String
            get() = role.toString() + userUriInsights
    }
}