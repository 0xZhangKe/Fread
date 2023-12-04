package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.TimelineStatusDataSource
import com.zhangke.utopia.activitypub.app.internal.model.UserStatusDataSource
import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class GetUserStatusDataSourceFromUriUseCase @Inject constructor(
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
) {

    operator fun invoke(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        getUserDataSource(uri)?.let { return it }
        getTimelineDataSource(uri)?.let { return it }
        return null
    }

    private fun getTimelineDataSource(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        val timelineUri = parseUriToTimelineUriUseCase(uri) ?: return null
        return TimelineStatusDataSource(
            host = timelineUri.timelineServerHost,
            type = timelineUri.type,
            activityPubStatusAdapter = activityPubStatusAdapter,
            clientManager = clientManager,
        )
    }

    private fun getUserDataSource(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        val userUri = parseUriToUserUriUseCase(uri) ?: return null
        return UserStatusDataSource(
            host = userUri.finger.host,
            userId = userUri.userId,
            activityPubStatusAdapter = activityPubStatusAdapter,
            clientManager = clientManager,
        )
    }
}
