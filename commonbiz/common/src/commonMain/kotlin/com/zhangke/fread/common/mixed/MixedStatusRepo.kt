package com.zhangke.fread.common.mixed

import com.zhangke.fread.common.db.MixedStatusDatabases
import com.zhangke.fread.common.db.MixedStatusEntity
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.model.PagedData
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope

class MixedStatusRepo (
    private val statusProvider: StatusProvider,
    mixedStatusDatabases: MixedStatusDatabases,
) {

    private val mixedStatusDao = mixedStatusDatabases.mixedStatusDao()

    fun getLocalStatusFlow(content: MixedContent): Flow<List<StatusUiState>> {
        return flow {
            mixedStatusDao.queryFlow(content.sourceUriList)
                .collect { emit(calculateDisplayList(it)) }
        }
    }

    private fun calculateDisplayList(statusList: List<MixedStatusEntity>): List<StatusUiState> {
        val endingList = getAllCrackEndingEntity(statusList)
        if (endingList.isEmpty()) return statusList.map { it.status }
        return statusList.subListOrNot(0, statusList.indexOf(endingList.first()) + 1)
            .map { it.status }
    }

    suspend fun refresh(content: MixedContent): Result<Unit> {
        if (content.sourceUriList.isEmpty()) return Result.success(Unit)
        val statusResolver = statusProvider.statusResolver
        val allResult = supervisorScope {
            content.sourceUriList.map { sourceUri ->
                async {
                    sourceUri to statusResolver.getStatusList(
                        uri = sourceUri,
                        limit = StatusConfigurationDefault.config.loadFromServerLimit,
                    )
                }
            }
        }.awaitAll()
        if (allResult.all { it.second.isFailure }) {
            return Result.failure(allResult.first().second.exceptionOrNull()!!)
        }
        allResult.mapNotNull { (sourceUri, result) ->
            result.getOrNull()?.toEntityList(sourceUri)
        }.flatten().let {
            mixedStatusDao.deleteStatusOfSources(content.sourceUriList)
            mixedStatusDao.insertStatus(it)
        }
        return Result.success(Unit)
    }

    suspend fun loadMoreStatus(
        content: MixedContent,
    ): Result<Unit> {
        val statusList = mixedStatusDao.queryAll(content.sourceUriList)
        if (statusList.isEmpty()) return Result.success(Unit)
        val endingList = getAllCrackEndingEntity(statusList)
        if (endingList.isEmpty()) return Result.success(Unit)
        val allResult = supervisorScope {
            endingList.take(3).map { entity ->
                async {
                    entity.sourceUri to statusProvider.statusResolver.getStatusList(
                        uri = entity.sourceUri,
                        limit = StatusConfigurationDefault.config.loadFromServerLimit,
                        maxId = entity.cursor,
                    )
                }
            }
        }.awaitAll()
        if (allResult.all { it.second.isFailure }) {
            return Result.failure(allResult.first().second.exceptionOrNull()!!)
        }
        allResult.mapNotNull { (sourceUri, result) ->
            result.getOrNull()?.toEntityList(sourceUri)
        }.flatten().let { mixedStatusDao.insertStatus(it) }
        return Result.success(Unit)
    }

    suspend fun updateStatus(status: StatusUiState) {
        mixedStatusDao.queryByStatusId(status.status.id)
            .map { it.copy(status = status) }
            .let { mixedStatusDao.insertStatus(it) }
    }

    suspend fun deleteStatus(statusId: String) {
        val entityList = mixedStatusDao.queryByStatusId(statusId)
        if (entityList.isEmpty()) return
        val newStatusList = mutableListOf<MixedStatusEntity>()
        entityList.groupBy { it.sourceUri }
            .filter { it.value.isNotEmpty() }
            .map { it.value.sortedByDescending { item -> item.createAt } }
            .forEach { statusList ->
                val lastEntity = statusList.last()
                if (lastEntity.statusId == statusId) {
                    var list = statusList.subList(0, statusList.lastIndex)
                    if (!lastEntity.cursor.isNullOrEmpty()) {
                        list = list.mapIndexed { index, entity ->
                            if (index == list.lastIndex) {
                                entity.copy(cursor = lastEntity.cursor)
                            } else {
                                entity
                            }
                        }
                    }
                    newStatusList.addAll(list)
                } else {
                    newStatusList.addAll(statusList)
                }
            }
        mixedStatusDao.insertStatus(newStatusList)
    }

    /**
     * 获取每个 Source 下对应的帖子列表中最早的那个帖子，如果这个帖子可以加载更多（包含 cursor）。
     */
    private fun getAllCrackEndingEntity(list: List<MixedStatusEntity>): List<MixedStatusEntity> {
        val endingList = mutableListOf<MixedStatusEntity>()
        val groupedList = list.groupBy { it.sourceUri }
        groupedList.forEach { (_, entities) ->
            entities.minByOrNull { it.createAt }
                ?.takeIf { !it.cursor.isNullOrEmpty() }
                ?.let { endingList += it }
        }
        return endingList.sortedByDescending { it.createAt }
    }

    private fun PagedData<StatusUiState>.toEntityList(sourceUri: FormalUri): List<MixedStatusEntity> {
        return this.list.sortedByDescending { it.status.createAt.epochMillis }
            .mapIndexed { index, item ->
                MixedStatusEntity(
                    statusId = item.status.id,
                    status = item,
                    createAt = item.status.createAt.epochMillis,
                    sourceUri = sourceUri,
                    cursor = if (index == this.list.lastIndex) this.cursor else null,
                )
            }
    }

    private fun <T> List<T>.subListOrNot(fromIndex: Int, toIndex: Int): List<T> {
        if (fromIndex < 0 || fromIndex > toIndex || fromIndex > size) return this
        if (toIndex < 0 || toIndex > size) return this
        return subList(fromIndex, toIndex)
    }
}