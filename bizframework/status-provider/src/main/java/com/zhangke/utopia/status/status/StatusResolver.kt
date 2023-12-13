package com.zhangke.utopia.status.status

import com.zhangke.framework.collections.mapFirst
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri

class StatusResolver(
    private val resolverList: List<IStatusResolver>,
) {

    suspend fun getStatusList(
        uri: StatusProviderUri,
        limit: Int,
        sinceId: String? = null,
    ): Result<List<Status>> {
        for (statusResolver in resolverList) {
            statusResolver.getStatusList(uri, limit, sinceId)?.let { return it }
        }
        return Result.failure(IllegalArgumentException("Unsupported uri:$uri!"))
    }

    /**
     * check this status is the first status of this source
     */
    suspend fun checkIsFirstStatus(sourceUri: StatusProviderUri, statusId: String): Result<Boolean> {
        return resolverList.mapFirst {
            it.checkIsFirstStatus(sourceUri, statusId)
        }
    }
}

interface IStatusResolver {

    /**
     * @return null if un-support
     */
    suspend fun getStatusList(uri: StatusProviderUri, limit: Int, sinceId: String?): Result<List<Status>>?

    suspend fun checkIsFirstStatus(sourceUri: StatusProviderUri, statusId: String): Result<Boolean>?
}
