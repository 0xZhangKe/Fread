package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetNewerStatusFromServerUseCase @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
    private val statusProvider: StatusProvider,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
    private val getPreviousStatusFromLocal: GetNewerStatusFromLocalUseCase,
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        val sinceStatus = statusContentRepo.query(sinceId) ?: return Result.failure(
            IllegalArgumentException("Can't find $sinceId in local record!")
        )
        val resultList = statusContentRepo.query(feedsConfig.sourceUriList)
            .groupBy { it.sourceUri }
            .mapNotNull { (uri, statusList) ->
                val thisSinceStatus = statusList.chooseSinceStatus(sinceStatus, limit)
                if (thisSinceStatus != null) {
                    loadPreviousStatus(uri, thisSinceStatus, limit)
                } else {
                    null
                }
            }
        if (resultList.all { it.isFailure }) {
            return Result.failure(resultList.first { it.isFailure }.exceptionOrNull()!!)
        }
        return Result.success(getPreviousStatusFromLocal(feedsConfig, sinceId, limit))
    }

    private fun List<StatusContentEntity>.chooseSinceStatus(
        sinceStatus: StatusContentEntity,
        limit: Int,
    ): StatusContentEntity? {
        if (size == 1) return first()
        val startIndex = indexOfFirst { it.createTimestamp <= sinceStatus.createTimestamp }
        var index = startIndex
        if (index < 0) return null
        while (index >= 0) {
            if (index == lastIndex) {
                index--
            } else if (get(index).nextStatusId.isNullOrEmpty()) {
                index++
                break
            } else {
                index--
            }
        }
        val sinceIndex = index.coerceAtLeast(0)
        if (startIndex - sinceIndex > limit) {
            return null
        }
        return get(sinceIndex)
    }

    private suspend fun loadPreviousStatus(
        sourceUri: FormalUri,
        sinceStatus: StatusContentEntity,
        limit: Int,
    ): Result<Unit> {
        val result = statusProvider.statusResolver.getStatusList(
            uri = sourceUri,
            minId = sinceStatus.id,
            limit = limit,
        ).map { list ->
            list.map {
                statusContentEntityAdapter.toEntity(
                    sourceUri = sourceUri,
                    status = it,
                    nextStatusId = sinceStatus.id,
                )
            }
        }
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val statusList = result.getOrNull()!!
        if (statusList.isEmpty()) return Result.success(Unit)
        saveStatusListToLocal(
            statusList = statusList,
            sinceId = null,
            nextIdOfLatest = sinceStatus.id,
        )
        return Result.success(Unit)
    }
}
