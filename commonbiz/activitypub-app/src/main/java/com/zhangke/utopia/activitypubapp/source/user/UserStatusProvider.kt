package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.status_provider.Status
import com.zhangke.utopia.status_provider.StatusProvider

class UserStatusProvider: StatusProvider {

    override suspend fun requestStatuses(): Result<List<Status>> {
        TODO("Not yet implemented")
    }
}