package com.zhangke.utopia.common.status.usecase.newer

import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetSingleSourceNewerStatusUseCase @Inject constructor(
    private val getNewerStatusFromLocal: GetNewerStatusFromLocalUseCase,
    private val syncNewerStatus: SyncNewerStatusUseCase,
){

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        minStatus: StatusContentEntity,
    ): Result<List<StatusContentEntity>> {

        val statusList = if (minStatus == null) {
            statusContentRepo.queryNewer(sourceUri, limit)
        } else {
            statusContentRepo.queryNewer(sourceUri, minStatus.createTimestamp, limit)
        }
        return Result.success(statusList)
    }
}
