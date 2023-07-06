package com.zhangke.utopia.activitypubapp.usecase

import com.zhangke.activitypub.entry.ActivityPubAccountEntity
import com.zhangke.utopia.activitypubapp.uri.user.ActivityPubUserUri
import javax.inject.Inject

class ActivityPubAccountToUriUseCase @Inject constructor(
    private val toWebFingerUseCase: ActivityPubAccountToWebFingerUseCase,
) {

    fun adapt(entity: ActivityPubAccountEntity): ActivityPubUserUri {
        return ActivityPubUserUri.create(entity.id, toWebFingerUseCase(entity))
    }
}
