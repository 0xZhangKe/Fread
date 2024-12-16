package com.zhangke.fread.common.status.usecase.previous

import com.zhangke.fread.common.db.StatusContentEntity
import com.zhangke.fread.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import me.tatarka.inject.annotations.Inject

class GetPreviousStatusUseCase @Inject constructor(
    private val getSingleSourcePreviousStatus: GetSingleSourcePreviousStatusUseCase,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    suspend operator fun invoke(
        sourceUriList: List<FormalUri>,
        limit: Int,
        maxStatus: StatusContentEntity,
    ): Result<List<Status>> {
        val maxCreateTime = maxStatus.createTimestamp
        val allDeferred = supervisorScope {
            sourceUriList.map { sourceUri ->
                async {
                    getSingleSourcePreviousStatus(
                        sourceUri = sourceUri,
                        limit = limit,
                        maxCreateTime = maxCreateTime,
                    )
                }
            }
        }
        val resultList = try {
            allDeferred.awaitAll()
        } catch (e: Throwable) {
            return Result.failure(e)
        }
        if (resultList.all { it.isFailure }) {
            return Result.failure(resultList.first().exceptionOrNull()!!)
        }
        val statusList = resultList.asSequence()
            .flatMap { it.getOrNull() ?: emptyList() }
            .filter { it.id != maxStatus.id }
            .sortedByDescending { it.createTimestamp }
            .take(limit)
            .map(statusContentEntityAdapter::toStatus)
            .toList()
        return Result.success(statusList)
    }
}
