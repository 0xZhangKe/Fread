package com.zhangke.fread.activitypub.app.internal.usecase.content

import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.model.PlatformLocator

class GetUserCreatedListUseCase (
    private val clientManager: ActivityPubClientManager
) {

    suspend operator fun invoke(locator: PlatformLocator): Result<List<ActivityPubListEntity>> {
        return clientManager.getClient(locator)
            .listsRepo
            .getAccountLists()
    }
}