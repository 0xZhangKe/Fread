package com.zhangke.utopia.common.status.usecase.newer

import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class SyncNewerStatusUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        minStatus: StatusContentEntity,
    ): Result<List<StatusContentEntity>> {
        val statusList = statusProvider.getNewerStatus(sourceUri, limit, minStatus)
        return Result.success(statusList)
    }
}
