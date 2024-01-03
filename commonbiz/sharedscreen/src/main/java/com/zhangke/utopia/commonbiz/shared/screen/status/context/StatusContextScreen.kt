package com.zhangke.utopia.commonbiz.shared.screen.status.context

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.core.net.toUri
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.utopia.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.utopia.commonbiz.shared.screen.R
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import kotlinx.coroutines.flow.SharedFlow

class StatusContextScreen(private val status: Status) : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val transparentNavigator = LocalTransparentNavigator.current
        val viewModel: StatusContextViewModel = getViewModel()
        viewModel.anchorStatus = status
        LaunchedEffect(status) {
            viewModel.onPrepared()
        }
        val uiState by viewModel.uiState.collectAsState()
        StatusContextContent(
            loadableState = uiState,
            snackbarMessageFlow = viewModel.errorMessageFlow,
            onMediaClick = { event ->
                when (event) {
                    is BlogMediaClickEvent.BlogImageClickEvent -> {
                        transparentNavigator.push(
                            ImageViewerScreen(
                                mediaList = event.mediaList,
                                selectedIndex = event.index,
                                coordinatesList = event.coordinatesList,
                                onDismiss = event.onDismiss,
                            )
                        )
                    }

                    is BlogMediaClickEvent.BlogVideoClickEvent -> {
                        navigator.push(FullVideoScreen(event.media.url.toUri()))
                    }
                }
            },
            onBackClick = navigator::pop,
            onInteractive = viewModel::onInteractive,
        )
    }

    @Composable
    private fun StatusContextContent(
        loadableState: LoadableState<StatusContextUiState>,
        snackbarMessageFlow: SharedFlow<TextString>,
        onBackClick: () -> Unit = {},
        onMediaClick: OnBlogMediaClick,
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPaddings),
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
                    InlineVideoLazyColumn(
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
                                onMediaClick = onMediaClick,
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
        onMediaClick: OnBlogMediaClick,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
    ) {
        val blog = statusInContext.status.status.intrinsicBlog
        when (statusInContext.type) {
            StatusInContextType.ANCESTOR -> AncestorBlogUi(
                modifier = modifier,
                blog = blog,
                indexInList = indexInList,
                isFirst = indexInList == 0,
                bottomPanelInteractions = statusInContext.status.bottomInteractions,
                moreInteractions = statusInContext.status.moreInteractions,
                onMediaClick = onMediaClick,
                onInteractive = {
                    onInteractive(statusInContext.status.status, it)
                },
            )

            StatusInContextType.ANCHOR -> AnchorBlogUi(
                modifier = modifier,
                blog = blog,
                indexInList = indexInList,
                showUpThread = indexInList > 0,
                onMediaClick = onMediaClick,
                bottomPanelInteractions = statusInContext.status.bottomInteractions,
                moreInteractions = statusInContext.status.moreInteractions,
                onInteractive = {
                    onInteractive(statusInContext.status.status, it)
                },
            )

            StatusInContextType.DESCENDANT -> DescendantStatusUi(
                modifier = modifier,
                blog = blog,
                bottomPanelInteractions = statusInContext.status.bottomInteractions,
                moreInteractions = statusInContext.status.moreInteractions,
                indexInList = indexInList,
                onMediaClick = onMediaClick,
                onInteractive = {
                    onInteractive(statusInContext.status.status, it)
                },
            )
        }
    }
}
