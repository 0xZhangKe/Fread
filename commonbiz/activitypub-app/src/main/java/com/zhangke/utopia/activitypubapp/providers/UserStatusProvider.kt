package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.utopia.activitypubapp.currentActivityPubClient
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.status_provider.IStatusProvider
import com.zhangke.utopia.status_provider.Status
import com.zhangke.utopia.status_provider.StatusSource

internal class UserStatusProvider(private val source: UserSource) : IStatusProvider {

    override fun applicable(source: StatusSource): Boolean {
        return source is UserSource
    }

    override suspend fun requestStatuses(source: StatusSource): Result<List<Status>> {
        source as UserSource
        var client = currentActivityPubClient
        if (client == null) {
            client = obtainActivityPubClient(source.webFinger.host)
        }
        return client.accountRepo
            .getStatuses(source.userId)
            .toStatus(client.application.host)
    }
}