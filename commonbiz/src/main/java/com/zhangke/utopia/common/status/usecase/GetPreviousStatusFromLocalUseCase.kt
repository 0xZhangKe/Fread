package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import javax.inject.Inject

class GetPreviousStatusFromLocalUseCase @Inject internal constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        maxId: String?,
        limit: Int,
    ): List<StatusContentEntity> {

        return emptyList()
    }

//    private suspend fun getStatusAfterMaxId(
//        feedsConfig: FeedsConfig,
//        maxId: String,
//        limit: Int,
//    ): List<StatusContentEntity> {
//        val statusEntity = statusContentRepo.querySourceById(maxId) ?: return emptyList()
//
//    }
}
