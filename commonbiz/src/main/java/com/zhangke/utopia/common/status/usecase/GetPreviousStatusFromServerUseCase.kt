package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import javax.inject.Inject
import kotlin.math.sin

internal class GetPreviousStatusFromServerUseCase @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        val sinceStatus = statusContentRepo.query(sinceId) ?: return Result.failure(IllegalArgumentException("Can't find ${sinceId} in local record!"))


        return Result.success(emptyList())
    }
}
