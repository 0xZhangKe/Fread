package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.utopia.blogprovider.Status
import com.zhangke.utopia.blogprovider.StatusProvider

/**
 * Created by ZhangKe on 2022/12/9.
 */
class ActivityPubTimelineProvider(
    private val client: ActivityPubClient
) : StatusProvider {

    override suspend fun requestStatuses(): Result<List<Status>> {
        return client.timelinesRepo.localTimelines().map { it.map { item -> item.toStatus(client.application.domain) } }
    }
}