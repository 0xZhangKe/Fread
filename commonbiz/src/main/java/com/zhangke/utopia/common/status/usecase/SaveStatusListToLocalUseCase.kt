package com.zhangke.utopia.common.status.usecase

import com.zhangke.framework.collections.updateIndex
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import javax.inject.Inject

class SaveStatusListToLocalUseCase @Inject internal constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    suspend operator fun invoke(
        statusList: List<StatusContentEntity>,
        sinceId: String? = null,
        nextIdOfLatest: String? = null,
    ) {
        if (statusList.isEmpty()) return
        if (sinceId != null) {
            updateStatusNextId(sinceId, statusList.first().id)
        }
        var entityList = statusList
        if (nextIdOfLatest != null) {
            entityList = entityList.updateIndex(statusList.lastIndex) {
                it.copy(nextStatusId = nextIdOfLatest)
            }
        } else if (entityList.last().nextStatusId.isNullOrEmpty()) {
            val nextStatusId = getLocalNextId(entityList.last().id)
            if (nextStatusId != null) {
                entityList = entityList.updateIndex(entityList.lastIndex) {
                    it.copy(nextStatusId = nextStatusId)
                }
            }
        }
        statusContentRepo.insert(entityList)
    }

    private suspend fun getLocalNextId(statusId: String): String? {
        val entity = statusContentRepo.query(statusId) ?: return null
        return entity.nextStatusId
    }

    private suspend fun updateStatusNextId(statusId: String, nextStatusId: String) {
        val entity = statusContentRepo.query(statusId)
            ?.copy(nextStatusId = nextStatusId) ?: return
        statusContentRepo.insert(entity)
    }
}
