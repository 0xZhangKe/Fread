package com.zhangke.utopia.activitypubapp.status

import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.protocol.isTimelineSourceUri
import com.zhangke.utopia.activitypubapp.protocol.isUserSource
import com.zhangke.utopia.activitypubapp.protocol.parseTimeline
import com.zhangke.utopia.activitypubapp.protocol.parseUserInfo
import com.zhangke.utopia.status.status.IGetStatusDataSourceByUriUseCase
import com.zhangke.utopia.status.status.Status
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

@Filt
class GetUserStatusDataSourceFromUriUseCase @Inject constructor(
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) : IGetStatusDataSourceByUriUseCase {

    override fun invoke(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        getUserDataSource(uri)?.let { return it }
        getTimelineDataSource(uri)?.let { return it }
        return null
    }

    private fun getTimelineDataSource(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        if (!uri.isTimelineSourceUri()) return null
        val (url, type) = uri.parseTimeline() ?: return null
        return TimelineStatusDataSource(
            host = url.host,
            type = type,
            activityPubStatusAdapter = activityPubStatusAdapter,
            obtainActivityPubClientUseCase = obtainActivityPubClientUseCase,
        )
    }

    private fun getUserDataSource(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        if (!uri.isUserSource()) return null
        val (webFinger, userId) = uri.parseUserInfo() ?: return null
        return UserStatusDataSource(
            host = webFinger.host,
            userId = userId,
            activityPubStatusAdapter = activityPubStatusAdapter,
            obtainActivityPubClientUseCase = obtainActivityPubClientUseCase,
        )
    }
}
