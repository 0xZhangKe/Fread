package com.zhangke.fread.common.feeds.repo

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.fread.common.status.repo.StatusContentRepo
import com.zhangke.fread.common.status.usecase.RefreshStatusUseCase
import com.zhangke.fread.common.status.usecase.previous.GetPreviousStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * 1. Load all local data.
 * 2. Load remote first page data and replace local data.
 *  data of remote needs to sort by date for ui layer.
 * 3. Load more remote data need decide max-id.
 */
@ApplicationScope
class FeedsRepo @Inject constructor(
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
        coroutineScope.launch(Dispatchers.IO) {
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
            .distinctBy { it.id }
    }

    suspend fun getStatus(
        sourceUriList: List<FormalUri>,
        maxId: String,
    ): Result<List<Status>> {
        val maxStatus = statusContentRepo.queryByPlatformId(maxId)
        if (maxStatus == null || maxStatus.isFirstStatus) {
            return Result.success(emptyList())
        }
        return getPreviousStatus(
            sourceUriList = sourceUriList,
            maxStatus = maxStatus,
        ).map { list -> list.distinctBy { it.id } }
    }

    suspend fun refresh(
        sourceUriList: List<FormalUri>,
    ): Result<RefreshResult> {
        return refreshStatus(sourceUriList)
    }

    suspend fun updateStatus(status: Status) {
        val existStatus = statusContentRepo.query(status.id) ?: return
        val newStatus = existStatus.copy(status = status)
        statusContentRepo.insert(newStatus)
    }
}
