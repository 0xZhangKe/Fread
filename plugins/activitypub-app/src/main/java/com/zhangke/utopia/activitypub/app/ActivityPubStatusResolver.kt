package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusContextUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetUserStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.IsTimelineFirstStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.IsUserFirstStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import com.zhangke.utopia.status.status.model.StatusInteraction
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubStatusResolver @Inject constructor(
    private val getUserStatus: GetUserStatusUseCase,
    private val getTimelineStatus: GetTimelineStatusUseCase,
    private val userUriTransformer: UserUriTransformer,
    private val timelineUriTransformer: TimelineUriTransformer,
    private val isUserFirstStatus: IsUserFirstStatusUseCase,
    private val isTimelineFirstStatus: IsTimelineFirstStatusUseCase,
    private val statusInteractive: StatusInteractiveUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val getStatusSupportInteraction: GetStatusInteractionUseCase,
    private val getStatusContextUseCase: GetStatusContextUseCase,
) : IStatusResolver {

    override suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        sinceId: String?,
        maxId: String?,
    ): Result<List<Status>>? {
        val userInsights = userUriTransformer.parse(uri)
        if (userInsights != null) {
            return getUserStatus(
                userInsights = userInsights,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
        }
        val timelineInsights = timelineUriTransformer.parse(uri)
        if (timelineInsights != null) {
            return getTimelineStatus(
                serverBaseUrl = timelineInsights.serverBaseUrl,
                type = timelineInsights.type,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
        }
        return null
    }

    override suspend fun checkIsFirstStatus(sourceUri: FormalUri, statusId: String): Result<Boolean>? {
        val userInsights = userUriTransformer.parse(sourceUri)
        if (userInsights != null) {
            return isUserFirstStatus(userInsights, statusId)
        }
        val timelineInsights = timelineUriTransformer.parse(sourceUri)
        if (timelineInsights != null) {
            return isTimelineFirstStatus(timelineInsights, statusId)
        }
        return null
    }

    override suspend fun interactive(
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status>? {
        if (status.notThisPlatform()) return null
        return statusInteractive(status, interaction).map { entity ->
            val platform = status.platform
            val supportActions = getStatusSupportInteraction(entity)
            activityPubStatusAdapter.toStatus(entity, platform, supportActions)
        }
    }

    override suspend fun getStatusContext(status: Status): Result<StatusContext>? {
        if (status.notThisPlatform()) return null
        return getStatusContextUseCase(status)
    }

    private fun Status.notThisPlatform(): Boolean {
        return this.platform.protocol != ACTIVITY_PUB_PROTOCOL
    }
}
