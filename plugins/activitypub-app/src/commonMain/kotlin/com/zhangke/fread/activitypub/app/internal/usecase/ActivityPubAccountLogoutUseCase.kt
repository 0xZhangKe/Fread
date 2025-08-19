package com.zhangke.fread.activitypub.app.internal.usecase

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.push.ActivityPubPushManager
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class ActivityPubAccountLogoutUseCase @Inject constructor(
    private val activityPubPushManager: ActivityPubPushManager,
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val loggedAccountProvider: LoggedAccountProvider,
) {

    suspend operator fun invoke(account: ActivityPubLoggedAccount) {
        invoke(
            baseUrl = account.platform.baseUrl,
            accountUri = account.uri,
            userId = account.userId,
        )
    }

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        accountUri: FormalUri,
        userId: String,
    ) {
        val role = PlatformLocator(baseUrl = baseUrl, accountUri = accountUri)
        activityPubPushManager.unsubscribe(role, userId)
        accountRepo.deleteByUri(accountUri)
        loggedAccountProvider.removeAccount(accountUri)
    }
}
