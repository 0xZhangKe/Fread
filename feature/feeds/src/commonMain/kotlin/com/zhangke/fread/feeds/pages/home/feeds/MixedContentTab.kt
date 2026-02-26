package com.zhangke.fread.feeds.pages.home.feeds

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.common.ContentToolbar
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.doubleTapToScrollTop
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

internal class MixedContentTab(
    private val configId: String,
    private val isLatestTab: Boolean,
) : Tab {

    override val options: TabOptions?
        @Composable get() = null

    @Composable
    override fun Content() {
        val snackBarHostState = rememberSnackbarHostState()
        val viewModel = koinViewModel<MixedContentViewModel>().getSubViewModel(configId)
        val uiState by viewModel.uiState.collectAsState()
        ConsumeSnackbarFlow(snackBarHostState, viewModel.errorMessageFlow)
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
        MixedContentUi(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onTitleClick = viewModel::onContentTitleClick,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MixedContentUi(
        uiState: MixedContentUiState,
        snackBarHostState: SnackbarHostState,
        onTitleClick: () -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        composedStatusInteraction: ComposedStatusInteraction,
    ) {
        val mainTabConnection = LocalNestedTabConnection.current
        val coroutineScope = rememberCoroutineScope()
        val topBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                ContentToolbar(
                    modifier = Modifier.doubleTapToScrollTop {
                        coroutineScope.launch {
                            mainTabConnection.scrollToTop()
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    title = uiState.content?.name.orEmpty(),
                    showNextIcon = !isLatestTab && uiState.showNextButton,
                    showRefreshButton = uiState.showRefreshButton,
                    account = null,
                    showAccountInfo = false,
                    onMenuClick = {
                        coroutineScope.launch {
                            mainTabConnection.openDrawer()
                        }
                    },
                    onNextClick = {
                        coroutineScope.launch {
                            mainTabConnection.switchToNextTab()
                        }
                    },
                    onRefreshClick = {
                        coroutineScope.launch {
                            mainTabConnection.scrollToTop()
                            onRefresh()
                        }
                    },
                    onTitleClick = onTitleClick,
                    onDoubleClick = {
                        coroutineScope.launch {
                            mainTabConnection.scrollToTop()
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(
                    modifier = Modifier.padding(bottom = 68.dp),
                    hostState = snackBarHostState,
                )
            },
        ) { paddings ->
            CompositionLocalProvider(
                LocalContentPadding provides paddings
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val nestedTabConnection = LocalNestedTabConnection.current
                    FeedsContent(
                        feeds = uiState.dataList,
                        refreshing = uiState.refreshing,
                        loadMoreState = uiState.loadMoreState,
                        showPagingLoadingPlaceholder = uiState.initializing,
                        pageErrorContent = uiState.pageError,
                        newStatusNotifyFlow = null,
                        onRefresh = onRefresh,
                        onLoadMore = onLoadMore,
                        composedStatusInteraction = composedStatusInteraction,
                        observeScrollToTopEvent = true,
                        onImmersiveEvent = {
                            if (it) {
                                mainTabConnection.openImmersiveMode(coroutineScope)
                            } else {
                                mainTabConnection.closeImmersiveMode(coroutineScope)
                            }
                        },
                        onScrollInProgress = {
                            nestedTabConnection.updateContentScrollInProgress(it)
                        },
                    )
                }
            }
        }
    }
}
