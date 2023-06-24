package com.zhangke.utopia.activitypubapp.status

import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.uri.timeline.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypubapp.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.status.IGetStatusDataSourceByUriUseCase
import com.zhangke.utopia.status.status.Status
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

@Filt
class GetUserStatusDataSourceFromUriUseCase @Inject constructor(
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
) : IGetStatusDataSourceByUriUseCase {

    override fun invoke(uri: StatusProviderUri): StatusDataSource<*, Status>? {
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
            obtainActivityPubClientUseCase = obtainActivityPubClientUseCase,
        )
    }

    private fun getUserDataSource(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        val userUri = parseUriToUserUriUseCase(uri) ?: return null
        return UserStatusDataSource(
            host = userUri.finger.host,
            userId = userUri.userId,
            activityPubStatusAdapter = activityPubStatusAdapter,
            obtainActivityPubClientUseCase = obtainActivityPubClientUseCase,
        )
    }
}
