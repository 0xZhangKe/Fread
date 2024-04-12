package com.zhangke.utopia.explore.screens.home.tab

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.krouter.KRouter
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.handle
import com.zhangke.utopia.explore.model.ExplorerItem
import com.zhangke.utopia.explore.usecase.GetExplorerItemUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

class ExplorerFeedsViewModel(
    private val type: ExplorerFeedsTabType,
    private val accountUri: FormalUri,
    private val statusProvider: StatusProvider,
    private val interactiveHandler: InteractiveHandler,
    private val getExplorerItem: GetExplorerItemUseCase,
) : SubViewModel() {

    var baseUrl: FormalBaseUrl? = null
        private set

    private val loadController = CommonLoadableController<ExplorerItem>(viewModelScope)

    val uiState: StateFlow<CommonLoadableUiState<ExplorerItem>> get() = loadController.uiState

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow.asSharedFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow.asSharedFlow()

    private var _account: LoggedAccount? = null

    init {
        launchInViewModel {
            baseUrl = statusProvider.accountManager
                .getAllLoggedAccount()
                .firstOrNull { it.uri == accountUri }
                ?.platform
                ?.baseUrl
            if (baseUrl == null) {
                loadController.mutableUiState.update {
                    it.copy(
                        errorMessage = textOf("No account found for $accountUri"),
                    )
                }
                return@launchInViewModel
            }
            loadController.initData(
                getDataFromServer = { getExplorerItem(baseUrl!!, type, 0, "") },
                getDataFromLocal = null,
            )
        }
    }

    fun onRefresh() {
        val baseUrl = baseUrl ?: return
        loadController.onRefresh(false) {
            getExplorerItem(baseUrl, type, 0, "")
        }
    }

    fun onLoadMore() {
        val baseUrl = baseUrl ?: return
        val dataList = uiState.value.dataList
        if (dataList.isEmpty()) return
        loadController.onLoadMore {
            getExplorerItem(
                baseUrl = baseUrl,
                type = type,
                offset = loadController.uiState.value.dataList.size,
                sinceId = dataList.last().id,
            )
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        launchInViewModel {
            interactiveHandler.onStatusInteractive(status, uiInteraction).handleResult()
        }
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        launchInViewModel {
            interactiveHandler.onUserInfoClick(blogAuthor).handleResult()
        }
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        launchInViewModel { interactiveHandler.onVoted(status, options).handleResult() }
    }

    fun onHashtagClick(hashtag: Hashtag) {
        launchInViewModel {
            statusProvider.screenProvider.getTagTimelineScreenRoute(hashtag)
                ?.let { KRouter.route<Screen>(it) }
                ?.let { _openScreenFlow.emit(it) }
        }
    }

    fun onFollowClick(blogAuthor: BlogAuthor) {
        updateFollowRelationship(blogAuthor, true)
    }

    fun onUnfollowClick(blogAuthor: BlogAuthor) {
        updateFollowRelationship(blogAuthor, false)
    }

    private fun updateFollowRelationship(
        blogAuthor: BlogAuthor,
        follow: Boolean,
    ) {
        launchInViewModel {
            val account = getAccount() ?: return@launchInViewModel
            val result = if (follow) {
                interactiveHandler.onFollowClick(account, blogAuthor)
            } else {
                interactiveHandler.onUnfollowClick(account, blogAuthor)
            }
            result.onSuccess {
                loadController.mutableUiState.update { state ->
                    val dataList = state.dataList.map {
                        if (it is ExplorerItem.ExplorerUser && it.user.uri == blogAuthor.uri) {
                            it.copy(following = follow)
                        } else {
                            it
                        }
                    }
                    state.copy(dataList = dataList)
                }
            }.onFailure { e ->
                e.toTextStringOrNull()?.let { _errorMessageFlow.emit(it) }
            }
        }
    }

    private suspend fun getAccount(): LoggedAccount? {
        val account = _account ?: statusProvider.accountManager.getAllLoggedAccount()
            .firstOrNull { it.uri == accountUri }
        _account = account
        return account
    }

    private fun List<ExplorerItem>.updateStatus(newStatus: StatusUiState): List<ExplorerItem> {
        return map { item ->
            if (item is ExplorerItem.ExplorerStatus && item.status.status.id == newStatus.status.id) {
                item.copy(status = newStatus)
            } else {
                item
            }
        }
    }

    private suspend fun InteractiveHandleResult.handleResult() {
        this.handle(
            uiStatusUpdater = { newStatusUiState ->
                loadController.mutableUiState.update {
                    it.copy(dataList = it.dataList.updateStatus(newStatusUiState))
                }
            },
            messageFlow = _errorMessageFlow,
            openScreenFlow = _openScreenFlow,
        )
    }
}
