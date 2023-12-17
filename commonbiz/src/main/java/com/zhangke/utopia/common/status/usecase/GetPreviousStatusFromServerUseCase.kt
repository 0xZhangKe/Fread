package com.zhangke.utopia.common.status.usecase

import com.zhangke.framework.utils.collect
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetPreviousStatusFromServerUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusContentRepo: StatusContentRepo,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
) {

    companion object {

        private const val FETCH_FROM_SERVER_LIMIT = 100
    }

    private val statusResolver get() = statusProvider.statusResolver

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String?,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        val resultList = getUriToSinceId(
            sinceId = sinceId,
            sourceUriList = feedsConfig.sourceUriList,
        ).map { (uri, sinceId) ->
            getStatusFromServer(sourceUri = uri, sinceId = sinceId)
        }
        if (resultList.all { it.isFailure }) {
            return Result.failure(resultList.first { it.isFailure }.exceptionOrNull()!!)
        }
        val statusList = resultList.collect()
            .getOrThrow()
            .sortedByDescending { it.createTimestamp }
            .take(limit)
        return Result.success(statusList)
    }

    private suspend fun getStatusFromServer(
        sourceUri: FormalUri,
        sinceId: String?,
    ): Result<List<StatusContentEntity>> {
        val statusListResult = statusResolver.getStatusList(
            uri = sourceUri,
            limit = FETCH_FROM_SERVER_LIMIT,
            sinceId = sinceId,
        ).map { list ->
            list.map {
                statusContentEntityAdapter.toEntity(
                    sourceUri = sourceUri,
                    status = it,
                    nextStatusId = null,
                )
            }
        }
        val statusList = statusListResult.getOrNull() ?: return statusListResult
        if (statusList.isEmpty()) return statusListResult
        val nextIdOfLatest = if (statusList.size < FETCH_FROM_SERVER_LIMIT) {
            val isFirstStatus = statusResolver.checkIsFirstStatus(sourceUri, statusList.last().id)
                .getOrNull() == true
            if (isFirstStatus) StatusContentRepo.STATUS_END_MAGIC_NUMBER else null
        } else {
            null
        }
        saveStatusListToLocal(
            statusList = statusList,
            sinceId = sinceId,
            nextIdOfLatest = nextIdOfLatest,
        )
        return statusListResult
    }

    /**
     * @return size of return list maybe not same with size of sourceUriList params
     */
    private suspend fun getUriToSinceId(
        sinceId: String?,
        sourceUriList: List<FormalUri>,
    ): List<Pair<FormalUri, String?>> {
        if (sinceId.isNullOrEmpty()) {
            return sourceUriList.map { it to null }
        }
        val list = mutableListOf<Pair<FormalUri, String?>>()
        val sinceStatus =
            statusContentRepo.query(sinceId) ?: return sourceUriList.map { it to null }
        val minTime = sinceStatus.createTimestamp
        val statusList = statusContentRepo.queryAfter(
            sourceUriList = sourceUriList,
            createTimestamp = minTime,
        )
        sourceUriList.forEach { uri ->
            // 对于没有已经没有下一个数据的 status，不再请求
            val thisStatus = statusList.lastOrNull { it.sourceUri == uri }
            if (thisStatus?.nextStatusId == StatusContentRepo.STATUS_END_MAGIC_NUMBER) {
                return@forEach
            }
            list += if (uri == sinceStatus.sourceUri) {
                uri to sinceId
            } else {
                uri to thisStatus?.id
            }
        }
        return list
    }
}
