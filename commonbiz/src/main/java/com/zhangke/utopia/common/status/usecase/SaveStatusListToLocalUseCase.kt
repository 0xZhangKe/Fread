package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class SaveStatusListToLocalUseCase @Inject internal constructor(
    private val statusContentRepo: StatusContentRepo,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    suspend operator fun invoke(
        statusSourceUri: FormalUri,
        statusList: List<Status>,
        sinceId: String? = null,
        nextIdOfLatest: String? = null,
    ): List<StatusContentEntity> {
        if (statusList.isEmpty()) return emptyList()
        if (sinceId != null) {
            updateStatusNextId(sinceId, statusList.first().id)
        }
        val finalNextIdOfLatest = nextIdOfLatest ?: getLocalNextId(statusList.last().id)
        val entityList = statusContentEntityAdapter.toEntityList(
            sourceUri = statusSourceUri,
            statusList = statusList,
            nextIdOfLatest = finalNextIdOfLatest,
        )
        statusContentRepo.insert(entityList)
        return entityList
    }

    private suspend fun getLocalNextId(statusId: String): String? {
        val entity = statusContentRepo.querySourceById(statusId) ?: return null
        return entity.nextStatusId
    }

    private suspend fun updateStatusNextId(statusId: String, nextStatusId: String) {
        val entity = statusContentRepo.querySourceById(statusId)
            ?.copy(nextStatusId = nextStatusId) ?: return
        statusContentRepo.insert(entity)
    }
}
