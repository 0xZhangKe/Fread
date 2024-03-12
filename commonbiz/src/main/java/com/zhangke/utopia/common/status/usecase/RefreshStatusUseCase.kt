package com.zhangke.utopia.common.status.usecase

import com.zhangke.framework.utils.collect
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

/**
 * 刷新给定的订阅源，返回此次刷新到的所有数据。
 * 刷新与清理本地缓存的逻辑如下：
 * 为了保证数据的连续性，当获取到刷新数据之后，用新的与本地数据做对比，如果新的数据最早的那条的时间大于
 * 本地数据最近的时间，就认为这两份数据不连续，此时清空本地数据，然后插入新数据，这样可以保证数据仍然是连续的。
 */
internal class RefreshStatusUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusContentRepo: StatusContentRepo,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    suspend operator fun invoke(
        sourceUriList: List<FormalUri>,
        limit: Int,
    ): Result<List<Status>> {
        val resultList = sourceUriList.map {
            getStatus(it, limit)
        }
        if (resultList.all { it.isFailure }) {
            return Result.failure(resultList.first().exceptionOrNull()!!)
        }
        val entities = resultList.collect().getOrThrow()
        val statusList = entities.sortedByDescending { it.createTimestamp }
            .map(statusContentEntityAdapter::toStatus)
        return Result.success(statusList)
    }

    private suspend fun getStatus(
        sourceUri: FormalUri,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        val entitiesResult = statusProvider.statusResolver
            .getStatusList(
                uri = sourceUri,
                limit = limit,
            ).map { list ->
                list.map {
                    statusContentEntityAdapter.toEntity(sourceUri, it, false)
                }
            }
        if (entitiesResult.isFailure) {
            return Result.failure(entitiesResult.exceptionOrNull()!!)
        }
        val entities = entitiesResult.getOrThrow()
        if (entities.isEmpty()) return Result.success(emptyList())
        val newStatusMinDateTime = entities.minBy { it.createTimestamp }.createTimestamp
        val localMaxDateTime = statusContentRepo.query(sourceUri)
            .maxByOrNull { it.createTimestamp }
            ?.createTimestamp
        if (localMaxDateTime != null && newStatusMinDateTime > localMaxDateTime) {
            // 数据不连续，则清空本地数据
            statusContentRepo.deleteBySource(sourceUri)
        }
        statusContentRepo.insert(entities)
        return Result.success(entities)
    }
}
