package com.zhangke.fread.activitypub.app.internal.usecase.content

import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class GetUserCreatedListUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager
) {

    suspend operator fun invoke(role: IdentityRole): Result<List<ActivityPubListEntity>> {
        return clientManager.getClient(role)
            .accountRepo
            .getAccountLists()
    }
}
