package com.zhangke.fread.common.status.usecase.previous

import com.zhangke.fread.common.db.StatusContentEntity
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.common.status.repo.StatusContentRepo
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class GetPreviousStatusFromLocalUseCase @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    /**
     * 获取本地的Status。
     * 根据传入的 maxCreateTime 参数，获取在该时间之前的帖子。
     */
    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        maxCreateTime: Long?,
    ): List<StatusContentEntity> {
        val realLimit = limit + StatusConfigurationDefault.config.loadFromLocalRedundancies
        return if (maxCreateTime == null) {
            statusContentRepo.query(sourceUri, realLimit)
        } else {
            statusContentRepo.queryPrevious(sourceUri, maxCreateTime, realLimit)
        }
    }
}
