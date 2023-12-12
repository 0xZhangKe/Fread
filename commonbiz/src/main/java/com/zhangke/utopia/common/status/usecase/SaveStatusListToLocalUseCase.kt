package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class SaveStatusListToLocalUseCase @Inject internal constructor(
    private val statusContentRepo: StatusContentRepo,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    suspend operator fun invoke(
        statusSourceUri: StatusProviderUri,
        statusList: List<Status>,
        previousId: String? = null,
        nextIdOfLatest: String? = null,
    ) {
        if (statusList.isEmpty()) return
        if (previousId != null) {
            updateStatusNextId(previousId, statusList.first().id)
        }
        val entityList = statusContentEntityAdapter.toEntityList(
            sourceUri = statusSourceUri,
            statusList = statusList,
            nextIdOfLatest = nextIdOfLatest,
        )
        statusContentRepo.insert(entityList)
    }

    private suspend fun updateStatusNextId(statusId: String, nextStatusId: String) {
        val entity = statusContentRepo.querySourceById(statusId)
            ?.copy(nextStatusId = nextStatusId) ?: return
        statusContentRepo.insert(entity)
    }
}
