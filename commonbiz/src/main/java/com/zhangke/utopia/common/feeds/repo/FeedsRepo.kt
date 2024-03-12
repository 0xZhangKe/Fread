package com.zhangke.utopia.common.feeds.repo

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.usecase.RefreshStatusUseCase
import com.zhangke.utopia.common.status.usecase.newer.GetNewerStatusUseCase
import com.zhangke.utopia.common.status.usecase.previous.GetPreviousStatusUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedsRepo @Inject internal constructor(
    private val getPreviousStatus: GetPreviousStatusUseCase,
    private val getNewerStatusUseCase: GetNewerStatusUseCase,
    private val statusContentRepo: StatusContentRepo,
    private val refreshStatus: RefreshStatusUseCase,
    private val statusProvider: StatusProvider,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    companion object {

        private const val DEFAULT_PAGE_SIZE = 40
    }

    /**
     * 目前，知识 BlogAuthor 发生变化会通知
     */
    private val _feedsInfoChangedFlow = MutableSharedFlow<Unit>()
    val feedsInfoChangedFlow = _feedsInfoChangedFlow.asSharedFlow()

    suspend fun onAppCreate() {
        statusProvider.statusSourceResolver
            .getAuthorUpdateFlow()
            .collect {
                statusContentRepo.updateAuthor(it)
                _feedsInfoChangedFlow.emit(Unit)
            }
    }

    suspend fun getLocalFirstPageStatus(
        sourceUriList: List<FormalUri>,
        limit: Int
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
            maxId = maxId,
        )
    }

    suspend fun refresh(
        sourceUriList: List<FormalUri>,
        limit: Int,
    ): Result<List<Status>> {
        return refreshStatus(sourceUriList, limit)
    }

    suspend fun getNewerStatus(
        sourceUriList: List<FormalUri>,
        minStatusId: String,
        limit: Int = DEFAULT_PAGE_SIZE,
    ): Result<List<Status>> {
        return getNewerStatusUseCase(
            sourceUriList = sourceUriList,
            limit = limit,
            minStatusId = minStatusId,
        )
    }

    suspend fun updateStatus(status: Status) {
        val existStatus = statusContentRepo.query(status.id) ?: return
        val newStatus = existStatus.copy(status = status)
        statusContentRepo.insert(newStatus)
    }
}
