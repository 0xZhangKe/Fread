package com.zhangke.utopia.status.status

import com.zhangke.framework.collections.mapFirst
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import com.zhangke.utopia.status.status.model.StatusInteraction
import com.zhangke.utopia.status.uri.FormalUri

class StatusResolver(
    private val resolverList: List<IStatusResolver>,
) {

    suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        sinceId: String? = null,
        maxId: String? = null,
    ): Result<List<Status>> {
        for (statusResolver in resolverList) {
            val result = statusResolver.getStatusList(
                uri = uri,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
            if (result != null) return result
        }
        return Result.failure(IllegalArgumentException("Unsupported uri:$uri!"))
    }

    /**
     * check this status is the first status of this source
     */
    suspend fun checkIsFirstStatus(status: Status): Result<Boolean> {
        return resolverList.mapFirst {
            it.checkIsFirstStatus(status)
        }
    }

    suspend fun interactive(status: Status, interaction: StatusInteraction): Result<Status> {
        return resolverList.mapFirst { it.interactive(status, interaction) }
    }

    suspend fun getStatusContext(status: Status): Result<StatusContext> {
        return resolverList.mapFirst { it.getStatusContext(status) }
    }
}

interface IStatusResolver {

    /**
     * @return null if un-support
     */
    suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        sinceId: String?,
        maxId: String?
    ): Result<List<Status>>?

    suspend fun checkIsFirstStatus(status: Status): Result<Boolean>?

    suspend fun interactive(status: Status, interaction: StatusInteraction): Result<Status>?

    suspend fun getStatusContext(status: Status): Result<StatusContext>?
}
