package com.zhangke.utopia.repo

import com.zhangke.utopia.status.resolvers.ISourceMaintainerResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceMaintainer
import com.zhangke.utopia.status.resolvers.StatusSourceResolver
import javax.inject.Inject

class StatusSourceRepo @Inject constructor(
    private val sourceResolver: StatusSourceResolver,
    private val sourMaintainerResolver: ISourceMaintainerResolver
) {

    suspend fun resolve(sourceUri: String): Result<StatusSource?> {
        return runCatching { sourceResolver.resolve(sourceUri) }
    }

    suspend fun searchSourceMaintainer(query: String): Result<StatusSourceMaintainer?> {
        return runCatching { sourMaintainerResolver.resolve(query) }
    }
}