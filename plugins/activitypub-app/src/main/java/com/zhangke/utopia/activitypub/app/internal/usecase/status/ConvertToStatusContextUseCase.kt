package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubStatusContextEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import javax.inject.Inject

class ConvertToStatusContextUseCase @Inject constructor(
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) {

    suspend operator fun invoke(
        entity: ActivityPubStatusContextEntity,
        platform: BlogPlatform,
    ): StatusContext {
        return StatusContext(
            ancestors = entity.ancestors.toStatusList(platform),
            descendants = entity.descendants.toStatusList(platform),
        )
    }

    private suspend fun List<ActivityPubStatusEntity>.toStatusList(
        platform: BlogPlatform,
    ): List<Status> {
        return this.map { statusEntity ->
            activityPubStatusAdapter.toStatus(
                entity = statusEntity,
                platform = platform,
            )
        }
    }
}
