package com.zhangke.utopia.common.feeds.repo

import com.zhangke.utopia.common.status.repo.StatusContentRepo
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
    private val getPreviousStatusUseCase: GetPreviousStatusUseCase,
    private val getNewerStatusUseCase: GetNewerStatusUseCase,
    private val statusContentRepo: StatusContentRepo,
    private val statusProvider: StatusProvider,
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

    suspend fun getPreviousStatus(
        sourceUriList: List<FormalUri>,
        limit: Int = DEFAULT_PAGE_SIZE,
        maxId: String? = null,
    ): Result<List<Status>> {
        return getPreviousStatusUseCase(
            sourceUriList = sourceUriList,
            limit = limit,
            maxId = maxId,
        )
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
