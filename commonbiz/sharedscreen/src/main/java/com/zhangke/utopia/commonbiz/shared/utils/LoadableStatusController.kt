package com.zhangke.utopia.commonbiz.shared.utils

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.handle
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.richtext.preParseRichText
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

open class LoadableStatusController(
    protected val coroutineScope: CoroutineScope,
    private val interactiveHandler: InteractiveHandler?,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) {

    private val loadableController = CommonLoadableController<StatusUiState>(coroutineScope)

    val mutableUiState = loadableController.mutableUiState
    val uiState = loadableController.uiState

    val mutableErrorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = mutableErrorMessageFlow

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow

    open fun initData(
        getDataFromServer: suspend () -> Result<List<Status>>,
        getDataFromLocal: (suspend () -> List<Status>)? = null,
    ) {
        loadableController.initData(
            getDataFromServer = {
                getDataFromServer().map { list ->
                    list.preParseRichText()
                    list.map { buildStatusUiState(it) }
                }
            },
            getDataFromLocal = getDataFromLocal?.let {
                {
                    val list = it()
                    list.preParseRichText()
                    list.map { buildStatusUiState(it) }
                }
            },
        )
    }

    open fun onRefresh(
        refreshFunction: suspend () -> Result<List<Status>>,
    ) {
        loadableController.onRefresh {
            refreshFunction().map { list ->
                list.preParseRichText()
                list.map { buildStatusUiState(it) }
            }
        }
    }

    open fun onLoadMore(
        loadMoreFunction: suspend (maxId: String) -> Result<List<Status>>,
    ) {
        val latestId = loadableController.uiState.value.dataList.lastOrNull()?.status?.id ?: return
        loadableController.onLoadMore {
            loadMoreFunction(latestId).map { list ->
                list.preParseRichText()
                list.map { buildStatusUiState(it) }
            }
        }
    }

    open fun onInteractive(
        useAccount: LoggedAccount,
        status: Status,
        uiInteraction: StatusUiInteraction,
    ) {
        interactiveHandler ?: throw IllegalArgumentException("InteractiveHandler is not provided")
        coroutineScope.launch {
            interactiveHandler.onStatusInteractive(useAccount, status, uiInteraction).handleResult()
        }
    }

    open fun onUserInfoClick(blogAuthor: BlogAuthor) {
        interactiveHandler ?: throw IllegalArgumentException("InteractiveHandler is not provided")
        coroutineScope.launch {
            interactiveHandler.onUserInfoClick(blogAuthor).handleResult()
        }
    }

    open fun onVoted(status: Status, votedOption: List<BlogPoll.Option>) {
        interactiveHandler ?: throw IllegalArgumentException("InteractiveHandler is not provided")
        coroutineScope.launch {
            interactiveHandler.onVoted(status, votedOption).handleResult()
        }
    }

    private suspend fun InteractiveHandleResult.handleResult() {
        handle(
            messageFlow = mutableErrorMessageFlow,
            openScreenFlow = _openScreenFlow,
            mutableUiState = loadableController.mutableUiState,
        )
    }
}
