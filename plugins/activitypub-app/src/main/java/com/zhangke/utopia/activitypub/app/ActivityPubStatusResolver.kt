package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetUserStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.IsTimelineFirstStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.IsUserFirstStatusUseCase
import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubStatusResolver @Inject constructor(
    private val getUserStatus: GetUserStatusUseCase,
    private val getTimelineStatus: GetTimelineStatusUseCase,
    private val userUriTransformer: UserUriTransformer,
    private val timelineUriTransformer: TimelineUriTransformer,
    private val isUserFirstStatus: IsUserFirstStatusUseCase,
    private val isTimelineFirstStatus: IsTimelineFirstStatusUseCase,
) : IStatusResolver {

    override suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        sinceId: String?,
        minId: String?,
    ): Result<List<Status>>? {
        val userInsights = userUriTransformer.parse(uri)
        if (userInsights != null) {
            return getUserStatus(
                userUriInsights = userInsights,
                limit = limit,
                sinceId = sinceId,
                minId = minId,
            )
        }
        val timelineInsights = timelineUriTransformer.parse(uri)
        if (timelineInsights != null) {
            return getTimelineStatus(
                timelineUriInsights = timelineInsights,
                limit = limit,
                sinceId = sinceId,
                minId = minId,
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
}
