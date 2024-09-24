package com.zhangke.fread.activitypub.app.internal.screen.user

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class UserDetailContainerViewModel @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val userUriTransformer: UserUriTransformer,
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) : ContainerViewModel<UserDetailViewModel, UserDetailContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): UserDetailViewModel {
        return UserDetailViewModel(
            accountManager = accountManager,
            userUriTransformer = userUriTransformer,
            clientManager = clientManager,
            emojiEntityAdapter = emojiEntityAdapter,
            accountEntityAdapter = accountEntityAdapter,
            role = params.role,
            userUri = params.userUri,
            webFinger = params.webFinger,
        )
    }

    fun getViewModel(
        role: IdentityRole,
        userUri: FormalUri?,
        webFinger: WebFinger?,
    ): UserDetailViewModel {
        return obtainSubViewModel(Params(role, userUri, webFinger))
    }

    class Params(
        val role: IdentityRole,
        val userUri: FormalUri?,
        val webFinger: WebFinger?,
    ) : SubViewModelParams() {

        override val key: String
            get() = role.toString() + userUri + webFinger
    }
}
