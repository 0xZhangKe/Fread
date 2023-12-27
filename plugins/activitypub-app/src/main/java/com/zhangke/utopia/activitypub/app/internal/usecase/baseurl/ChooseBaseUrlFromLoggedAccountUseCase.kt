package com.zhangke.utopia.activitypub.app.internal.usecase.baseurl

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import javax.inject.Inject

class ChooseBaseUrlFromLoggedAccountUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
) {

    suspend operator fun invoke(): FormalBaseUrl? {
        accountManager.getActiveAccount()
            ?.baseUrl
            ?.let { return it }
        accountManager.getAllLoggedAccount()
            .firstOrNull()
            ?.baseUrl
            ?.let { return it }
        return null
    }
}
