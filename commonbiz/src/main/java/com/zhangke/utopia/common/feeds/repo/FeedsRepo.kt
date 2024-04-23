package com.zhangke.utopia.common.feeds.repo

import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.usecase.RefreshStatusUseCase
import com.zhangke.utopia.common.status.usecase.previous.GetPreviousStatusUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedsRepo @Inject internal constructor(
    private val getPreviousStatus: GetPreviousStatusUseCase,
    private val statusContentRepo: StatusContentRepo,
    private val refreshStatus: RefreshStatusUseCase,
    private val statusProvider: StatusProvider,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    /**
     * 目前，只有 BlogAuthor 发生变化会通知
     */
    private val _feedsInfoChangedFlow = MutableSharedFlow<Unit>()
    val feedsInfoChangedFlow = _feedsInfoChangedFlow.asSharedFlow()

    fun onAppCreate(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            statusProvider.statusSourceResolver
                .getAuthorUpdateFlow()
                .collect {
                    statusContentRepo.updateAuthor(it)
                    _feedsInfoChangedFlow.emit(Unit)
                }
        }
    }

    suspend fun getLocalFirstPageStatus(
        sourceUriList: List<FormalUri>,
        limit: Int,
    ): List<Status> {
        return statusContentRepo.query(sourceUriList, limit)
            .map { statusContentEntityAdapter.toStatus(it) }
    }

    suspend fun getStatus(
        sourceUriList: List<FormalUri>,
        limit: Int,
        maxId: String,
    ): Result<List<Status>> {
        val maxStatus = statusContentRepo.queryByPlatformId(maxId)
        if (maxStatus == null || maxStatus.isFirstStatus) {
            return Result.success(emptyList())
        }
        return getPreviousStatus(
            sourceUriList = sourceUriList,
            limit = limit,
            maxStatus = maxStatus,
        )
    }

    suspend fun refresh(
        sourceUriList: List<FormalUri>,
        limit: Int,
    ): Result<RefreshResult> {
        return refreshStatus(sourceUriList, limit)
    }

    suspend fun updateStatus(status: Status) {
        val existStatus = statusContentRepo.query(status.id) ?: return
        val newStatus = existStatus.copy(status = status)
        statusContentRepo.insert(newStatus)
    }
}
