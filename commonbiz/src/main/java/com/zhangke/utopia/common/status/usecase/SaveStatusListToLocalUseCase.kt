package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.common.utils.markToFirstStatus
import com.zhangke.utopia.status.StatusProvider
import javax.inject.Inject

internal class SaveStatusListToLocalUseCase @Inject internal constructor(
    private val statusContentRepo: StatusContentRepo,
    private val statusProvider: StatusProvider,
) {

    suspend operator fun invoke(
        statusList: List<StatusContentEntity>,
        maxId: String? = null,
        needCheckFirstStatus: Boolean = false,
    ) {
        if (statusList.isEmpty()) return
        if (maxId != null) {
            updateStatusNextId(maxId, statusList.first().id)
        }
        var entityList = updateEachStatusNextId(statusList)
        if (needCheckFirstStatus) {
            val firstStatus = entityList.minBy { it.createTimestamp }
            val markedStatus = markFirstStatus(firstStatus)
            entityList = entityList.map {
                if (it.id == markedStatus.id) {
                    markedStatus
                } else {
                    it
                }
            }
        }
        statusContentRepo.insert(entityList)
    }

    private fun updateEachStatusNextId(statusList: List<StatusContentEntity>): List<StatusContentEntity> {
        return statusList.mapIndexed { index, entity ->
            val nextStatusId =
                if (entity.nextStatusId.isNullOrEmpty() && index < statusList.lastIndex) {
                    statusList[index + 1].id
                } else {
                    entity.nextStatusId
                }
            entity.copy(nextStatusId = nextStatusId)
        }
    }

    private suspend fun updateStatusNextId(statusId: String, nextStatusId: String) {
        val entity = statusContentRepo.query(statusId)?.copy(nextStatusId = nextStatusId) ?: return
        statusContentRepo.insert(entity)
    }

    private suspend fun markFirstStatus(
        status: StatusContentEntity,
    ): StatusContentEntity {
        if (status.nextStatusId.isNullOrEmpty().not()) return status
        val existStatusEntity = statusContentRepo.query(status.id)
        if (existStatusEntity?.nextStatusId.isNullOrEmpty().not()) {
            return status.copy(nextStatusId = existStatusEntity?.nextStatusId)
        }
        val isFirstStatus = statusProvider.statusResolver.checkIsFirstStatus(
            sourceUri = status.sourceUri,
            statusId = status.statusIdOfPlatform,
        ).getOrNull() == true
        return if (isFirstStatus) {
            status.markToFirstStatus()
        } else {
            status
        }
    }
}
