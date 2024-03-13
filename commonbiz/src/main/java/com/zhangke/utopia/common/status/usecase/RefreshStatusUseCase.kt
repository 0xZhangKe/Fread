package com.zhangke.utopia.common.status.usecase

import android.util.Log
import com.zhangke.utopia.common.feeds.model.RefreshResult
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
    ): Result<RefreshResult> {
        Log.d("U_TEST", "Status Refresh, sourceUriList: $sourceUriList, limit: $limit.")
        val resultList = sourceUriList.map {
            getStatus(it, limit)
        }
        if (resultList.all { it.isFailure }) {
            return Result.failure(resultList.first().exceptionOrNull()!!)
        }
        val newStatusList = mutableListOf<Status>()
        val deletedStatusList = mutableListOf<Status>()
        resultList.mapNotNull { it.getOrNull() }
            .forEach {
                newStatusList.addAll(it.newStatus)
                deletedStatusList.addAll(it.deletedStatus)
            }
        val result = RefreshResult(
            newStatus = newStatusList.sortedByDescending { it.datetime },
            deletedStatus = deletedStatusList,
        )
        return Result.success(result)
    }

    private suspend fun getStatus(
        sourceUri: FormalUri,
        limit: Int,
    ): Result<RefreshResult> {
        val entitiesResult = fetchStatus(sourceUri, limit)
        if (entitiesResult.isFailure) {
            return Result.failure(entitiesResult.exceptionOrNull()!!)
        }
        val entities = entitiesResult.getOrThrow()
        if (entities.isEmpty()) return Result.success(RefreshResult.EMPTY)
        val newEntities = mutableListOf<StatusContentEntity>()
        val deleteEntities = mutableListOf<StatusContentEntity>()
        val localRecentStatus = statusContentRepo.queryRecentStatus(sourceUri)
        if (localRecentStatus == null) {
            // 本地无数据
            newEntities.addAll(entities)
        } else {
            val localInNewIndex = entities.indexOfFirst {
                it.id == localRecentStatus.id
            }
            if (localInNewIndex < 0) {
                // 本地最新数据不在新数据中，表示本地数据已经过期
                val allDeletedList = statusContentRepo.query(sourceUri)
                statusContentRepo.deleteBySource(sourceUri)
                deleteEntities.addAll(allDeletedList)
            } else {
                // 新数据与本地数据有重合，清除本地数据重复部分，然后插入全部新数据
                val repeatedList = entities.subList(localInNewIndex, entities.size)
                deleteEntities.addAll(repeatedList)
            }
            // 按照上面重合数据的替换逻辑，即使是在部分重合的情况下，新增数据仍然是全部。
            newEntities.addAll(entities)
        }
        statusContentRepo.insert(entities)
        val refreshResult = RefreshResult(
            newStatus = newEntities.map { statusContentEntityAdapter.toStatus(it) },
            deletedStatus = deleteEntities.map { statusContentEntityAdapter.toStatus(it) },
        )
        return Result.success(refreshResult)
    }

    private suspend fun fetchStatus(
        sourceUri: FormalUri,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        return statusProvider.statusResolver
            .getStatusList(
                uri = sourceUri,
                limit = limit,
            ).map { list ->
                list.map {
                    statusContentEntityAdapter.toEntity(sourceUri, it, false)
                }
            }
    }
}
