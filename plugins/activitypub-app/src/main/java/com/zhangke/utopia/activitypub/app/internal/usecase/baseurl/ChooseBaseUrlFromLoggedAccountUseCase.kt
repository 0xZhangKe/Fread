package com.zhangke.utopia.activitypub.app.internal.usecase.baseurl

import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import javax.inject.Inject

class ChooseBaseUrlFromLoggedAccountUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
) {

    suspend operator fun invoke(): String? {
        accountManager.getActiveAccount()
            ?.baseUrl
            ?.takeIf { it.isNotEmpty() }
            ?.let { return it }
        accountManager.getAllLoggedAccount()
            .firstOrNull()
            ?.baseUrl
            ?.takeIf { it.isNotEmpty() }
            ?.let { return it }
        return null
    }
}
