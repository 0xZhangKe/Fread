package com.zhangke.utopia.commonbiz.shared.screen.status.context

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.screen.R
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.BlogContentUi
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import kotlinx.coroutines.flow.SharedFlow

class StatusContextScreen(private val status: Status) : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: StatusContextViewModel = getViewModel()
        viewModel.anchorStatus = status
        LaunchedEffect(status) {
            viewModel.onPrepares()
        }
        val uiState by viewModel.uiState.collectAsState()
        StatusContextContent(
            loadableState = uiState,
            snackbarMessageFlow = viewModel.errorMessageFlow,
            onBackClick = navigator::pop,
            onInteractive = viewModel::onInteractive,
        )
    }

    @Composable
    private fun StatusContextContent(
        loadableState: LoadableState<StatusContextUiState>,
        snackbarMessageFlow: SharedFlow<TextString>,
        onBackClick: () -> Unit = {},
        onInteractive: (Status, StatusUiInteraction) -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        ConsumeSnackbarFlow(hostState = snackbarHostState, messageTextFlow = snackbarMessageFlow)
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                Toolbar(
                    title = stringResource(R.string.shared_status_context_screen_title),
                    onBackClick = onBackClick,
                )
            },
            content = { contentPaddings ->
                LoadableLayout(
                    modifier = Modifier.padding(contentPaddings),
                    state = loadableState,
                ) { uiState ->
                    val contextStatus = uiState.contextStatus
                    if (contextStatus.isEmpty()) return@LoadableLayout
                    val state = rememberLazyListState()
                    val anchorIndex = uiState.anchorIndex
                    if (anchorIndex in 0..contextStatus.lastIndex) {
                        LaunchedEffect(anchorIndex) {
                            state.animateScrollToItem(anchorIndex)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                    ) {
                        itemsIndexed(
                            items = contextStatus,
                        ) { index, statusInContext ->
                            StatusInContextUi(
                                modifier = Modifier.fillMaxWidth(),
                                statusInContext = statusInContext,
                                indexInList = index,
                                onInteractive = onInteractive,
                            )
                        }
                    }
                }
            },
        )
    }

    @Composable
    private fun StatusInContextUi(
        modifier: Modifier = Modifier,
        statusInContext: StatusInContext,
        indexInList: Int,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
    ) {
        when (statusInContext.type) {
            StatusInContextType.ANCHOR -> AnchorStatusUi(
                modifier = modifier,
                statusInContext = statusInContext,
                indexInList = indexInList,
                onMediaClick = {},
                onInteractive = { _, _ -> },
            )

            StatusInContextType.ANCESTOR -> AssociatedStatusUi(
                modifier = modifier,
                statusInContext = statusInContext,
                indexInList = indexInList,
                onMediaClick = {},
                onInteractive = { _, _ -> },
            )

            StatusInContextType.DESCENDANT -> AssociatedStatusUi(
                modifier = modifier,
                statusInContext = statusInContext,
                indexInList = indexInList,
                onMediaClick = {},
                onInteractive = { _, _ -> },
            )
        }
    }

    @Composable
    private fun AnchorStatusUi(
        modifier: Modifier,
        statusInContext: StatusInContext,
        indexInList: Int,
        onMediaClick: OnBlogMediaClick,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
    ) {
        val statusUiState = statusInContext.status
        val status = statusUiState.status
        val blog = (status as Status.NewBlog).blog
        BlogContentUi(
            modifier = modifier,
            blog = blog,
            bottomPanelInteractions = statusUiState.bottomInteractions,
            moreInteractions = statusUiState.moreInteractions,
            onInteractive = {
                onInteractive(status, it)
            },
            indexInList = indexInList,
            onMediaClick = onMediaClick,
        )
    }

    @Composable
    private fun AssociatedStatusUi(
        modifier: Modifier,
        statusInContext: StatusInContext,
        indexInList: Int,
        onMediaClick: OnBlogMediaClick,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
    ) {

    }
}
