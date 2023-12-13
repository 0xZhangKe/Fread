package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetUserStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.IsTimelineFirstStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.IsUserFirstStatusUseCase
import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubStatusResolver @Inject constructor(
    private val getUserStatus: GetUserStatusUseCase,
    private val getTimelineStatus: GetTimelineStatusUseCase,
    private val userUriTransformer: UserUriTransformer,
    private val timelineUriTransformer: TimelineUriTransformer,
    private val isUserFirstStatus: IsUserFirstStatusUseCase,
    private val isTimelineFirstStatus: IsTimelineFirstStatusUseCase,
) : IStatusResolver {

    override suspend fun getStatusList(uri: StatusProviderUri, limit: Int, sinceId: String?): Result<List<Status>>? {
        val userInsights = userUriTransformer.parse(uri)
        if (userInsights != null) {
            return getUserStatus(userInsights, limit, sinceId)
        }
        val timelineInsights = timelineUriTransformer.parse(uri)
        if (timelineInsights != null) {
            return getTimelineStatus(timelineInsights, limit, sinceId)
        }
        return null
    }

    override suspend fun checkIsFirstStatus(sourceUri: StatusProviderUri, statusId: String): Result<Boolean>? {
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
