package com.zhangke.utopia.common.status.usecase.previous

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetPreviousStatusUseCase @Inject constructor(
    private val getSingleSourcePreviousStatus: GetSingleSourcePreviousStatusUseCase,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    suspend operator fun invoke(
        sourceUriList: List<FormalUri>,
        limit: Int,
        maxStatus: StatusContentEntity,
    ): Result<List<Status>> {
        val maxCreateTime = maxStatus.createTimestamp
        val resultList = sourceUriList.map { sourceUri ->
            getSingleSourcePreviousStatus(
                sourceUri = sourceUri,
                limit = limit,
                maxCreateTime = maxCreateTime,
            )
        }
        if (resultList.all { it.isFailure }) {
            return Result.failure(resultList.first().exceptionOrNull()!!)
        }
        val statusList = resultList.flatMap { it.getOrNull() ?: emptyList() }
            .filter { it.id != maxStatus.id }
            .sortedByDescending { it.createTimestamp }
            .take(limit)
            .map(statusContentEntityAdapter::toStatus)
        return Result.success(statusList)
    }
}
