package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.utopia.activitypubapp.currentActivityPubClient
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.UserSource
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.blogprovider.Status
import com.zhangke.utopia.blogprovider.StatusProvider

internal class UserStatusProvider(private val source: UserSource) : StatusProvider {

    override suspend fun requestStatuses(): Result<List<Status>> {
        var client = currentActivityPubClient
        if (client == null) {
            val url = ActivityPubUrl(source.metaSourceInfo.url)
            if (!url.validate()) {
                return Result.failure(IllegalStateException("Not Login!"))
            }
            client = obtainActivityPubClient(url.toughHost)
        }
        return client.accountRepo
            .getStatuses(source.userId)
            .toStatus(client.application.host)
    }
}