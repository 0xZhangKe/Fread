package com.zhangke.utopia.status.status

import android.util.Log
import com.zhangke.framework.collections.mapFirst
import com.zhangke.utopia.status.status.model.Status
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
            statusResolver.getStatusList(
                uri = uri,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )?.let {
                val logText = it.getOrNull()?.joinToString(",") { s -> s.id }
                Log.d("U_TEST", "getStatusList($uri, $sinceId) result is $logText")
                return it
            }
        }
        return Result.failure(IllegalArgumentException("Unsupported uri:$uri!"))
    }

    /**
     * check this status is the first status of this source
     */
    suspend fun checkIsFirstStatus(sourceUri: FormalUri, statusId: String): Result<Boolean> {
        return resolverList.mapFirst {
            it.checkIsFirstStatus(sourceUri, statusId)
        }
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

    suspend fun checkIsFirstStatus(sourceUri: FormalUri, statusId: String): Result<Boolean>?
}
