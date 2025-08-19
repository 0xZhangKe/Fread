package com.zhangke.fread.activitypub.app.internal.screen.user

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.activitypub.app.internal.usecase.ActivityPubAccountLogoutUseCase
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class UserDetailContainerViewModel @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val userUriTransformer: UserUriTransformer,
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    private val accountLogout: ActivityPubAccountLogoutUseCase,
) : ContainerViewModel<UserDetailViewModel, UserDetailContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): UserDetailViewModel {
        return UserDetailViewModel(
            accountManager = accountManager,
            userUriTransformer = userUriTransformer,
            clientManager = clientManager,
            emojiEntityAdapter = emojiEntityAdapter,
            accountEntityAdapter = accountEntityAdapter,
            accountLogout = accountLogout,
            locator = params.locator,
            userUri = params.userUri,
            webFinger = params.webFinger,
            userId = params.userId,
        )
    }

    fun getViewModel(
        locator: PlatformLocator,
        userUri: FormalUri?,
        webFinger: WebFinger?,
        userId: String?,
    ): UserDetailViewModel {
        return obtainSubViewModel(Params(locator, userUri, webFinger, userId))
    }

    class Params(
        val locator: PlatformLocator,
        val userUri: FormalUri?,
        val webFinger: WebFinger?,
        val userId: String?,
    ) : SubViewModelParams() {

        override val key: String
            get() = locator.toString() + userUri + webFinger + userId
    }
}
