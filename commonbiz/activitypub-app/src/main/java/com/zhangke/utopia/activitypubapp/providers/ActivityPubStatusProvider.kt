package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.utopia.activitypubapp.oauth.ActivityPubOAuthor
import com.zhangke.utopia.activitypubapp.user.ActivityPubUser
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.Status
import com.zhangke.utopia.status_provider.StatusProvider

/**
 * Created by ZhangKe on 2022/12/9.
 */
class ActivityPubStatusProvider(
    private val client: ActivityPubClient,
    private val source: StatusSource,
    private val user: ActivityPubUser?,
    private val oauthor: ActivityPubOAuthor
) : StatusProvider {

    override suspend fun requestStatuses(): Result<List<Status>> {
        return Result.success(emptyList())
    }
}