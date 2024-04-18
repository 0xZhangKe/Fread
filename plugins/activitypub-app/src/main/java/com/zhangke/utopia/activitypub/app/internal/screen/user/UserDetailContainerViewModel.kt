package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.emoji.MapAccountEntityEmojiUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.uri.FormalUri
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserDetailContainerViewModel @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val userUriTransformer: UserUriTransformer,
    private val clientManager: ActivityPubClientManager,
    private val mapAccountEntityEmoji: MapAccountEntityEmojiUseCase,
) : ContainerViewModel<UserDetailViewModel, UserDetailContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): UserDetailViewModel {
        return UserDetailViewModel(
            accountManager = accountManager,
            userUriTransformer = userUriTransformer,
            clientManager = clientManager,
            mapAccountEntityEmoji = mapAccountEntityEmoji,
            role = params.role,
            userUri = params.userUri,
        )
    }

    fun getViewModel(role: IdentityRole, userUri: FormalUri): UserDetailViewModel {
        return obtainSubViewModel(Params(role, userUri))
    }

    class Params(
        val role: IdentityRole,
        val userUri: FormalUri,
    ) : SubViewModelParams() {

        override val key: String
            get() = role.toString() + userUri
    }
}