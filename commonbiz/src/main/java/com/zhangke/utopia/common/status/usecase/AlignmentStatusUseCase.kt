package com.zhangke.utopia.common.status.usecase

import android.util.Log
import com.zhangke.framework.collections.mapFirst
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * 根据基准时间，向上或向下对其所有的 source 的 status。
 */
class AlignmentStatusUseCase @Inject internal constructor(
    private val statusProvider: StatusProvider,
    private val statusContentRepo: StatusContentRepo,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
) {

    suspend operator fun invoke(
        sourceUriList: List<FormalUri>,
        baselineId: String,
    ): Result<Unit> {
        Log.d("U_TEST", "AlignmentStatusUseCase: baselineId = $baselineId")
        val baselineEntity = statusContentRepo.query(baselineId) ?: return Result.success(Unit)
        Log.d("U_TEST", "AlignmentStatusUseCase: baselineEntity = $baselineEntity")
        val alignUpResult = alignUp(sourceUriList, baselineEntity)
        val alignmentDown = alignDown(sourceUriList, baselineEntity)
        if (alignUpResult.isFailure) return alignUpResult
        if (alignmentDown.isFailure) return alignmentDown
        return Result.success(Unit)
    }

    /**
     * 对齐所有最早那个帖子时间大于基准时间的Source。
     */
    private suspend fun alignDown(
        sourceUriList: List<FormalUri>,
        sinceSourceEntity: StatusContentEntity,
    ): Result<Unit> {
        val needLoadNextSourceToStatus = sourceUriList.map {
            it to statusContentRepo.queryFirst(it)
        }.filter { (_, entity) ->
            if (entity?.nextStatusId == StatusContentRepo.STATUS_END_MAGIC_NUMBER) {
                false
            } else {
                entity != null && entity.createTimestamp > sinceSourceEntity.createTimestamp
            }
        }
        if (needLoadNextSourceToStatus.isEmpty()) {
            Log.d("U_TEST", "do not need align down, since to $sinceSourceEntity")
            return Result.success(Unit)
        }
        Log.d("U_TEST", "start align down, since to $sinceSourceEntity")
        val resultList = coroutineScope {
            needLoadNextSourceToStatus.map {
                async { alignDownSourceToBaseline(it.first, it.second!!) }
            }.awaitAll()
        }
        if (resultList.any { it.isFailure }) {
            val e = resultList.mapFirst { it.exceptionOrNull() }
            Log.d("U_TEST", "align down failed, case ${e.message}, since to $sinceSourceEntity")
            return Result.failure(e)
        }
        Log.d("U_TEST", "align down success, since to $sinceSourceEntity")
        return Result.success(Unit)
    }

    private suspend fun alignDownSourceToBaseline(
        sourceUri: FormalUri,
        sinceSourceEntity: StatusContentEntity,
    ): Result<Unit> {
        val limit = StatusConfigurationDefault.config.loadFromServerLimit
        val result = statusProvider.statusResolver.getStatusList(
            uri = sourceUri,
            limit = limit,
            maxId = sinceSourceEntity.statusIdOfPlatform,
        )
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val resultStatusList = statusContentEntityAdapter.toEntityList(
            sourceUri = sourceUri,
            statusList = result.getOrNull()!!,
        )
        if (resultStatusList.isEmpty()) {
            if (sinceSourceEntity.nextStatusId.isNullOrEmpty()) {
                val isFirstStatus = statusProvider.statusResolver
                    .checkIsFirstStatus(
                        sourceUri = sourceUri,
                        statusId = sinceSourceEntity.statusIdOfPlatform,
                    )
                    .getOrNull() == true
                if (isFirstStatus) {
                    statusContentRepo.insert(sinceSourceEntity.copy(nextStatusId = StatusContentRepo.STATUS_END_MAGIC_NUMBER))
                }
            }
            return Result.failure(IllegalStateException("Can't load next page!"))
        }
        val nextIdOfLatest = if (resultStatusList.size < limit) {
            val isFirstStatus = statusProvider.statusResolver
                .checkIsFirstStatus(
                    sourceUri = sourceUri,
                    statusId = resultStatusList.last().statusIdOfPlatform,
                )
                .getOrNull() == true
            if (isFirstStatus) StatusContentRepo.STATUS_END_MAGIC_NUMBER else null
        } else {
            null
        }
        saveStatusListToLocal(
            statusList = resultStatusList,
            maxId = sinceSourceEntity.id,
            nextIdOfLatest = nextIdOfLatest,
        )
        val lastStatus = resultStatusList.last()
        if (resultStatusList.size < limit) {
            return if (lastStatus.createTimestamp <= sinceSourceEntity.createTimestamp) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Can't load next page!"))
            }
        }
        if (lastStatus.createTimestamp <= sinceSourceEntity.createTimestamp) {
            return Result.success(Unit)
        }
        return alignDownSourceToBaseline(sourceUri, lastStatus)
    }

    /**
     * 将最新一条早于基准时间的 source 向上对齐。
     */
    private suspend fun alignUp(
        sourceUriList: List<FormalUri>,
        sinceSourceEntity: StatusContentEntity,
    ): Result<Unit> {
        val needLoadPreviousSourceToStatus = sourceUriList.map {
            it to statusContentRepo.queryLatest(it)
        }.filter { (_, entity) ->
            entity != null && entity.createTimestamp < sinceSourceEntity.createTimestamp
        }
        if (needLoadPreviousSourceToStatus.isEmpty()) {
            Log.d("U_TEST", "do not need align up, since to $sinceSourceEntity")
            return Result.success(Unit)
        }
        Log.d("U_TEST", "start align up, since to $sinceSourceEntity")
        val resultList = coroutineScope {
            needLoadPreviousSourceToStatus.map {
                async { alignUpSourceToBaseline(it.first, it.second!!) }
            }.awaitAll()
        }
        if (resultList.any { it.isFailure }) {
            val e = resultList.mapFirst { it.exceptionOrNull() }
            Log.d("U_TEST", "align up failure, case ${e.message}, since is $sinceSourceEntity")
            return Result.failure(e)
        }
        Log.d("U_TEST", "align up success, since to $sinceSourceEntity")
        return Result.success(Unit)
    }

    private suspend fun alignUpSourceToBaseline(
        sourceUri: FormalUri,
        sinceSourceEntity: StatusContentEntity,
    ): Result<Unit> {
        val limit = StatusConfigurationDefault.config.loadFromServerLimit
        val result = statusProvider.statusResolver.getStatusList(
            uri = sourceUri,
            limit = limit,
            sinceId = sinceSourceEntity.statusIdOfPlatform,
        )
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val resultStatusList = statusContentEntityAdapter.toEntityList(
            sourceUri = sourceUri,
            statusList = result.getOrNull()!!,
        )
        if (resultStatusList.isEmpty()) {
            return Result.success(Unit)
        }
        saveStatusListToLocal(
            statusList = resultStatusList,
            maxId = null,
            nextIdOfLatest = sinceSourceEntity.id,
        )
        // latest is mean the order by time
        val latestStatus = resultStatusList.first()
        if (latestStatus.createTimestamp >= sinceSourceEntity.createTimestamp || resultStatusList.size < limit) {
            return Result.success(Unit)
        }
        return alignUpSourceToBaseline(
            sourceUri = sourceUri,
            sinceSourceEntity = latestStatus,
        )
    }
}
