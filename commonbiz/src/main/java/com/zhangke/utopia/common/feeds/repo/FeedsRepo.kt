package com.zhangke.utopia.common.feeds.repo

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.usecase.GetPreviousStatusUseCase
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class FeedsRepo @Inject internal constructor(
    private val getPreviousStatusUseCase: GetPreviousStatusUseCase,
) {

    suspend fun getPreviousStatus(
        feedsConfig: FeedsConfig,
        maxId: String? = null,
        limit: Int = 50,
    ): Result<List<Status>> {
        return getPreviousStatusUseCase(
            feedsConfig = feedsConfig,
            limit = limit,
            maxId = maxId,
        )
    }

//    suspend fun fetchStatusByFeedsConfig(
//        feedsConfig: FeedsConfig,
//        limit: Int = 30,
//    ): Result<Unit> {
//        val statusResolver = statusProvider.statusResolver
//        val resultList = coroutineScope {
//            feedsConfig.sourceUriList.map {
//                async {
//                    it to statusResolver.getStatusList(it, limit)
//                }
//            }.awaitAll()
//        }
//        resultList.forEach { (uri, result) ->
//            val list = result.getOrNull()
//            if (!list.isNullOrEmpty()) {
//                saveStatusContentToLocal(uri, list)
//            }
//        }
//        val hasSuccess = resultList.any { it.second.isSuccess }
//        val exception = resultList.mapFirstOrNull { it.second.exceptionOrNull() }
//        return if (hasSuccess) {
//            Result.success(Unit)
//        } else {
//            Result.failure(exception ?: IllegalStateException("fetch failed!"))
//        }
//    }

//    suspend fun loadMore(
//        feedsConfig: FeedsConfig,
//        latestStatusId: String,
//        limit: Int = 30,
//    ): Result<Unit> {
//        val nextId = statusLinkedRepo.getNextId(latestStatusId)
//        if (nextId == null) {
//            statusProvider.statusResolver.getStatusList()
//        }
//        return Result.success(Unit)
//    }
//
//    private suspend fun loadMoreByUri(
//        uri: StatusProviderUri,
//        latestStatusId: String,
//        limit: Int,
//    ): Result<Unit> {
//        val nextId = statusLinkedRepo.getNextId(latestStatusId)
//        if (nextId.isNullOrEmpty()){
//            statusProvider.statusResolver.getStatusList(uri = uri, limit = limit, sinceId = latestStatusId)
//        }else{
//
//        }
//    }

//    private suspend fun requestStatusFromFeedsConfig(
//        feedsConfig: FeedsConfig,
//        limit: Int = 30,
//    ): List<Pair<FormalUri, Result<List<Status>>>> {
//        val statusResolver = statusProvider.statusResolver
//        return coroutineScope {
//            feedsConfig.sourceUriList.map {
//                async {
//                    it to statusResolver.getStatusList(it, limit)
//                }
//            }.awaitAll()
//        }
//    }
//
//    private suspend fun saveStatusContentToLocal(uri: FormalUri, statusList: List<Status>) {
//        statusContentRepo.insert(uri, statusList)
//        val linkedList = statusList.mapIndexedNotNull { index, status ->
//            if (index == statusList.lastIndex) {
//                null
//            } else {
//                status.id to statusList[index + 1].id
//            }
//        }
//        statusLinkedRepo.insertList(linkedList)
//    }
}
