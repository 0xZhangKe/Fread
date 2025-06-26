package com.zhangke.fread.activitypub.app.internal.screen.explorer

import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ExplorerViewModel(
    private val clientManager: ActivityPubClientManager,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val accountAdapter: ActivityPubAccountEntityAdapter,
    private val hashtagAdapter: ActivityPubTagAdapter,
    private val statusProvider: StatusProvider,
    statusUpdater: StatusUpdater,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    refactorToNewStatus: RefactorToNewStatusUseCase,
    private val locator: PlatformLocator,
    private val platform: BlogPlatform,
    private val type: ExplorerFeedsTabType,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    companion object {

        private const val LIMIT = 50
    }

    private val loadController = CommonLoadableController<ExplorerItem>(
        viewModelScope,
        onPostSnackMessage = {
            launchInViewModel {
                mutableErrorMessageFlow.emit(it)
            }
        },
    )

    val uiState: StateFlow<CommonLoadableUiState<ExplorerItem>> get() = loadController.uiState

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { interactiveResult ->
                when (interactiveResult) {
                    is InteractiveHandleResult.UpdateStatus -> {
                        loadController.mutableUiState.update { state ->
                            val dataList = state.dataList.updateStatus(interactiveResult.status)
                            state.copy(dataList = dataList)
                        }
                    }

                    is InteractiveHandleResult.DeleteStatus -> {
                        loadController.mutableUiState.update { state ->
                            state.copy(
                                dataList = state.dataList.filter {
                                    if (it is ExplorerItem.ExplorerStatus) {
                                        it.id != interactiveResult.statusId
                                    } else {
                                        true
                                    }
                                }
                            )
                        }
                    }

                    is InteractiveHandleResult.UpdateFollowState -> {
                        val authorUri = interactiveResult.userUri
                        loadController.mutableUiState.update { state ->
                            val dataList = state.dataList.map {
                                if (it is ExplorerItem.ExplorerUser && it.user.uri == authorUri) {
                                    it.copy(following = interactiveResult.following)
                                } else {
                                    it
                                }
                            }
                            state.copy(dataList = dataList)
                        }
                    }
                }
            },
        )
        launchInViewModel {
            loadController.initData(
                getDataFromServer = { getExplorer(maxId = null, offset = 0) },
                getDataFromLocal = null,
            )
        }
    }

    fun onRefresh() {
        loadController.onRefresh(false) {
            getExplorer(null, 0)
        }
    }

    fun onLoadMore() {
        val dataList = uiState.value.dataList
        if (dataList.isEmpty()) return
        loadController.onLoadMore {
            getExplorer(
                maxId = dataList.last().id,
                offset = loadController.uiState.value.dataList.size,
            )
        }
    }

    private suspend fun getExplorer(maxId: String?, offset: Int): Result<List<ExplorerItem>> {
        val client = clientManager.getClient(locator)
        return when (type) {
            ExplorerFeedsTabType.STATUS -> {
                val loggedAccount = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
                client.timelinesRepo
                    .publicTimelines(limit = LIMIT, maxId = maxId)
                    .map { list ->
                        list.map {
                            activityPubStatusAdapter.toStatusUiState(
                                entity = it,
                                platform = platform,
                                locator = locator,
                                loggedAccount = loggedAccount,
                            )
                        }
                    }.map { list ->
                        list.map { ExplorerItem.ExplorerStatus(it) }
                    }
            }

            ExplorerFeedsTabType.USERS -> {
                if ((offset > 0) || !maxId.isNullOrEmpty()) {
                    return Result.success(emptyList())
                }
                client.accountRepo
                    .getSuggestions()
                    .map { list -> list.map { accountAdapter.toAuthor(it.account) } }
                    .map { list -> list.map { ExplorerItem.ExplorerUser(it, false) } }
            }

            ExplorerFeedsTabType.HASHTAG -> {
                client.instanceRepo
                    .getTrendsTags(limit = LIMIT, offset = offset)
                    .map { list -> list.map { hashtagAdapter.adapt(it) } }
                    .map { list -> list.map { ExplorerItem.ExplorerHashtag(it) } }
            }
        }
    }

    private fun List<ExplorerItem>.updateStatus(newStatus: StatusUiState): List<ExplorerItem> {
        return map { item ->
            if (item is ExplorerItem.ExplorerStatus && item.status.status.intrinsicBlog.id == newStatus.status.intrinsicBlog.id) {
                item.copy(status = newStatus)
            } else {
                item
            }
        }
    }
}
