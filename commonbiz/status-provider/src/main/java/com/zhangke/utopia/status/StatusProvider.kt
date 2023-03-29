package com.zhangke.utopia.status

import com.zhangke.utopia.status.source.StatusSourceUri
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ZhangKe on 2022/12/9.
 */
@Singleton
class StatusProvider @Inject constructor(
    private val providers: List<IStatusProvider>,
) {

    suspend fun requestStatuses(
        sourceUri: String,
    ): Result<List<Status>> {
        val uri = StatusSourceUri.create(sourceUri) ?: return Result.failure(
            IllegalArgumentException("$sourceUri is not validate uri!")
        )
        return providers.firstOrNull { it.applicable(uri) }
            ?.requestStatuses(uri)
            ?: Result.failure(
                IllegalArgumentException("$sourceUri does not have provider!")
            )
    }
}

interface IStatusProvider {

    fun applicable(sourceUri: StatusSourceUri): Boolean

    suspend fun requestStatuses(
        sourceUri: StatusSourceUri,
    ): Result<List<Status>>
}
