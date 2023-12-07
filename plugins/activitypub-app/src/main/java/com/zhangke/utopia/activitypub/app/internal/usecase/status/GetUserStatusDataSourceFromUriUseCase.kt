package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.TimelineStatusDataSource
import com.zhangke.utopia.activitypub.app.internal.model.UserStatusDataSource
import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class GetUserStatusDataSourceFromUriUseCase @Inject constructor(
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    private val userUriTransformer: UserUriTransformer,
    private val timelineUriTransformer: TimelineUriTransformer,
) {

    operator fun invoke(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        getUserDataSource(uri)?.let { return it }
        getTimelineDataSource(uri)?.let { return it }
        return null
    }

    private fun getTimelineDataSource(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        val timelineUriData = timelineUriTransformer.parse(uri) ?: return null
        return TimelineStatusDataSource(
            baseUrl = timelineUriData.serverBaseUrl,
            type = timelineUriData.type,
            activityPubStatusAdapter = activityPubStatusAdapter,
            clientManager = clientManager,
        )
    }

    private fun getUserDataSource(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        val userUriData = userUriTransformer.parse(uri) ?: return null
        return UserStatusDataSource(
            baseUrl = userUriData.webFinger.host.toBaseUrl(),
            userId = userUriData.userId,
            activityPubStatusAdapter = activityPubStatusAdapter,
            clientManager = clientManager,
        )
    }
}
