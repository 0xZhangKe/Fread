package com.zhangke.utopia.activitypub.app.internal.db.adapter

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusTableEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import javax.inject.Inject

class ActivityPubStatusTableEntityAdapter @Inject constructor(
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
) {

    fun toTableEntity(
        entity: ActivityPubStatusEntity,
        type: ActivityPubStatusSourceType,
        serverBaseUrl: FormalBaseUrl,
        listId: String? = null,
    ): ActivityPubStatusTableEntity {
        return ActivityPubStatusTableEntity(
            id = entity.id,
            type = type,
            serverBaseUrl = serverBaseUrl,
            listId = listId,
            status = entity,
            createTimestamp = formatDatetimeToDate(entity.createdAt).time,
        )
    }
}
