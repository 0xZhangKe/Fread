package com.zhangke.utopia.activitypub.app.internal.usecase.content

import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.status.account.unauthenticatedResult
import javax.inject.Inject

class GetUserCreatedListUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val clientManager: ActivityPubClientManager
) {

    suspend operator fun invoke(baseUrl: FormalBaseUrl): Result<List<ActivityPubListEntity>> {
        accountManager.getAllLoggedAccount()
            .firstOrNull { it.baseUrl == baseUrl }
            ?: return unauthenticatedResult("No user is currently logged in.")
        return clientManager.getClient(baseUrl)
            .accountRepo
            .getAccountLists()
    }
}
