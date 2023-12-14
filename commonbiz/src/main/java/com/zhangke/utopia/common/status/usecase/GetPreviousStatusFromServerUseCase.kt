package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import javax.inject.Inject

internal class GetPreviousStatusFromServerUseCase @Inject constructor(
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
){

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String,
        limit: Int,
    ): Result<List<StatusContentEntity>> {

        return Result.success(emptyList())
    }
}