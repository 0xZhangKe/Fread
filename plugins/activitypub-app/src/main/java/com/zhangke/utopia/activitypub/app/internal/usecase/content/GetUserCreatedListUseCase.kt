package com.zhangke.utopia.activitypub.app.internal.usecase.content

import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.status.model.IdentityRole
import javax.inject.Inject

class GetUserCreatedListUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager
) {

    suspend operator fun invoke(role: IdentityRole): Result<List<ActivityPubListEntity>> {
        return clientManager.getClient(role)
            .accountRepo
            .getAccountLists()
    }
}
