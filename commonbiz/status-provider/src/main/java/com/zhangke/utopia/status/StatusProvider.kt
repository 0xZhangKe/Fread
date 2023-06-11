package com.zhangke.utopia.status

import com.zhangke.utopia.status.source.StatusProviderUri
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ZhangKe on 2022/12/9.
 */
class StatusProvider @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards IStatusProvider>,
) {

    suspend fun requestStatuses(
        sourceUri: String,
    ): Result<List<Status>> {
        val uri = StatusProviderUri.create(sourceUri) ?: return Result.failure(
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

    fun applicable(sourceUri: StatusProviderUri): Boolean

    suspend fun requestStatuses(
        sourceUri: StatusProviderUri,
    ): Result<List<Status>>
}
