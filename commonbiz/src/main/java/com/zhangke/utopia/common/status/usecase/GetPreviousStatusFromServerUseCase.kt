package com.zhangke.utopia.common.status.usecase

import android.util.Log
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
    private val getPreviousStatusFromLocal: GetPreviousStatusFromLocalUseCase,
) {

    companion object {

        private const val FETCH_FROM_SERVER_LIMIT = 100
    }

    private val statusResolver get() = statusProvider.statusResolver

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        maxId: String?,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        Log.d("U_TEST", "GetPreviousStatusFromServer feeds is ${feedsConfig.name}, sinceId is $maxId")
        val resultList = getUriToSinceId(
            maxId = maxId,
            sourceUriList = feedsConfig.sourceUriList,
        ).map { (uri, sinceStatus) ->
            getStatusFromServer(sourceUri = uri, sinceStatus = sinceStatus)
        }
        if (resultList.all { it.isFailure }) {
            val e = resultList.first { it.isFailure }.exceptionOrNull()!!
            Log.d("U_TEST", "GetPreviousStatusFromServer all failure, cause ${e.message}")
            return Result.failure(e)
        }
        Log.d("U_TEST", "GetPreviousStatusFromServer statusList size is ${resultList.flatMap { it.getOrNull() ?: emptyList() }.size}")
        return Result.success(getPreviousStatusFromLocal(feedsConfig, maxId, limit))
    }

    private suspend fun getStatusFromServer(
        sourceUri: FormalUri,
        sinceStatus: StatusContentEntity?,
    ): Result<List<StatusContentEntity>> {
        val statusListResult = statusResolver.getStatusList(
            uri = sourceUri,
            limit = FETCH_FROM_SERVER_LIMIT,
            maxId = sinceStatus?.statusIdOfPlatform,
        ).map { list ->
            list.map {
                statusContentEntityAdapter.toEntity(
                    sourceUri = sourceUri,
                    status = it,
                    nextStatusId = null,
                )
            }
        }
        Log.d("U_TEST", "getStatusFromServer($sourceUri) since ${sinceStatus?.statusIdOfPlatform} result size is ${statusListResult.getOrNull()?.size}.")
        val statusList = statusListResult.getOrNull() ?: return statusListResult
        if (statusList.isEmpty()) return statusListResult
        val nextIdOfLatest = if (statusList.size < FETCH_FROM_SERVER_LIMIT) {
            val isFirstStatus = statusResolver.checkIsFirstStatus(sourceUri, statusList.last().statusIdOfPlatform)
                .getOrNull() == true
            if (isFirstStatus) StatusContentRepo.STATUS_END_MAGIC_NUMBER else null
        } else {
            null
        }
        Log.d("U_TEST", "nextIdOfLatest $nextIdOfLatest")
        saveStatusListToLocal(
            statusList = statusList,
            maxId = sinceStatus?.id,
            nextIdOfLatest = nextIdOfLatest,
        )
        return statusListResult
    }

    /**
     * @return size of return list maybe not same with size of sourceUriList params
     */
    private suspend fun getUriToSinceId(
        maxId: String?,
        sourceUriList: List<FormalUri>,
    ): List<Pair<FormalUri, StatusContentEntity?>> {
        if (maxId.isNullOrEmpty()) {
            return sourceUriList.map { it to null }
        }
        val list = mutableListOf<Pair<FormalUri, StatusContentEntity?>>()
        val sinceStatus = statusContentRepo.query(maxId)
        if (sinceStatus == null) {
            Log.d("U_TEST", "getUriToSinceId($maxId) cant query this since status from local.")
            return sourceUriList.map { it to null }
        }
        val minTime = sinceStatus.createTimestamp
        val statusList = statusContentRepo.queryNewer(
            sourceUriList = sourceUriList,
            createTimestamp = minTime,
        )
        Log.d("U_TEST", "getUriToSinceId($maxId) local statusList size is ${statusList.size}.")
        sourceUriList.forEach { uri ->
            val thisStatus = statusList.lastOrNull { it.sourceUri == uri }
            // 对于没有已经没有下一个数据的 status，不再请求
            if (thisStatus?.nextStatusId == StatusContentRepo.STATUS_END_MAGIC_NUMBER) {
                return@forEach
            }
            list += if (uri == sinceStatus.sourceUri) {
                uri to sinceStatus
            } else {
                uri to thisStatus
            }
        }
        return list
    }
}
