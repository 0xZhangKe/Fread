package com.zhangke.utopia.common.status.usecase

import android.util.Log
import com.zhangke.framework.collections.updateIndex
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import javax.inject.Inject

class SaveStatusListToLocalUseCase @Inject internal constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    suspend operator fun invoke(
        statusList: List<StatusContentEntity>,
        maxId: String? = null,
        nextIdOfLatest: String? = null,
    ) {
        Log.d("U_TEST", "SaveStatusListToLocal status size is ${statusList.size}, sinceId is $maxId, nextIdOfLatest is $nextIdOfLatest")
        if (statusList.isEmpty()) return
        if (maxId != null) {
            updateStatusNextId(maxId, statusList.first().id)
        }
        var entityList = updateNextIdOfLatest(statusList, nextIdOfLatest)
        entityList = updateEachStatusNextId(entityList)
        statusContentRepo.insert(entityList)
        Log.d("U_TEST", "insert to local success.")
    }

    private suspend fun updateNextIdOfLatest(
        statusList: List<StatusContentEntity>,
        nextIdOfLatest: String? = null,
    ): List<StatusContentEntity> {
        return if (nextIdOfLatest != null) {
            statusList.updateIndex(statusList.lastIndex) {
                it.copy(nextStatusId = nextIdOfLatest)
            }
        } else if (statusList.last().nextStatusId.isNullOrEmpty()) {
            val nextStatusId = getLocalNextId(statusList.last().id)
            Log.d("U_TEST", "local next id is $nextStatusId")
            if (nextStatusId != null) {
                statusList.updateIndex(statusList.lastIndex) {
                    it.copy(nextStatusId = nextStatusId)
                }
            } else {
                statusList
            }
        } else {
            statusList
        }
    }

    private fun updateEachStatusNextId(statusList: List<StatusContentEntity>): List<StatusContentEntity> {
        return statusList.mapIndexed { index, entity ->
            val nextStatusId = if (entity.nextStatusId.isNullOrEmpty() && index < statusList.lastIndex) {
                statusList[index + 1].id
            } else {
                entity.nextStatusId
            }
            entity.copy(nextStatusId = nextStatusId)
        }
    }

    private suspend fun getLocalNextId(statusId: String): String? {
        val entity = statusContentRepo.query(statusId) ?: return null
        return entity.nextStatusId
    }

    private suspend fun updateStatusNextId(statusId: String, nextStatusId: String) {
        val entity = statusContentRepo.query(statusId)?.copy(nextStatusId = nextStatusId)
        if (entity == null) {
            Log.d("U_TEST", "can query this since id from local.")
            return
        }
        Log.d("U_TEST", "update nextStatusId of entity to local.")
        statusContentRepo.insert(entity)
    }
}
