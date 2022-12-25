package com.zhangke.blogprovider.activitypubprovider

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.utopia.blogprovider.Status
import com.zhangke.utopia.blogprovider.StatusProvider

/**
 * Created by ZhangKe on 2022/12/9.
 */
class ActivityPubTimelineProvider(
    private val client: ActivityPubClient
) : StatusProvider {

    override suspend fun requestStatuses(): List<Status> {
        client.oauthRepo
        return emptyList()
    }
}