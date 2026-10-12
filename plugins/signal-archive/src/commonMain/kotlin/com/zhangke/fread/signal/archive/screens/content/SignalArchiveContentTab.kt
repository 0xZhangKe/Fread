package com.zhangke.fread.signal.archive.screens.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.plusContentPadding
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.signal.archive.content.SignalArchiveContent
import com.zhangke.fread.signal.archive.screens.ArchiveMessage
import com.zhangke.fread.signal.archive.screens.feeds.SignalArchiveFeedsTab
import com.zhangke.fread.signal.archive.screens.feeds.SignalArchiveFeedsTabInfo
import com.zhangke.fread.status.ui.common.HomeContentTabsTopBar
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class SignalArchiveContentTab(
    private val content: SignalArchiveContent,
    private val isLatestContent: Boolean,
) : BaseTab() {

    override val options: TabOptions
        @Composable get() = TabOptions(title = content.name)

    @Composable
    override fun Content() {
        val viewModel = koinViewModel<SignalArchiveContentViewModel>(key = content.id) {
            parametersOf(content)
        }
        val uiState by viewModel.uiState.collectAsState()
        SignalArchiveHomeContent(content, isLatestContent, uiState, viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignalArchiveHomeContent(
    content: SignalArchiveContent,
    isLatestContent: Boolean,
    uiState: SignalArchiveContentUiState,
    viewModel: SignalArchiveContentViewModel,
) {
    val scope = rememberCoroutineScope()
    val connection = LocalNestedTabConnection.current
    val config = koinInject<FreadConfigManager>()
    val showRefresh by config.homeTabRefreshButtonVisibleFlow.collectAsState()
    val showNext by config.homeTabNextButtonVisibleFlow.collectAsState()
    val nodes = uiState.index?.children.orEmpty()
    val pager = rememberPagerState { nodes.size }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val contentScrolling by connection.contentScrollInpProgress.collectAsState()
    LaunchedEffect(nodes, uiState.generation) {
        if (nodes.isNotEmpty()) pager.scrollToPage(uiState.selectedTab)
    }
    LaunchedEffect(pager.currentPage, nodes) {
        if (nodes.isNotEmpty()) viewModel.selectTab(pager.currentPage)
    }
    LaunchedEffect(connection) {
        connection.refreshFlow.collect { viewModel.refresh() }
    }
    LaunchedEffect(connection, scrollBehavior) {
        connection.scrollToTopFlow.collect {
            scrollBehavior.state.heightOffset = 0F
            scrollBehavior.state.contentOffset = 0F
        }
    }
    DisposableEffect(connection) {
        onDispose { connection.updateContentScrollInProgress(false) }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeContentTabsTopBar(
                title = content.name,
                account = null,
                showAccountInfo = false,
                selectedTabIndex = pager.currentPage.coerceIn(0, (nodes.size - 1).coerceAtLeast(0)),
                tabTitles = nodes.map { it.title },
                scrollBehavior = scrollBehavior,
                showNextIcon = !isLatestContent && showNext,
                showRefreshButton = showRefresh,
                onMenuClick = { scope.launch { connection.openDrawer() } },
                onRefreshClick = viewModel::refresh,
                onNextClick = { scope.launch { connection.switchToNextTab() } },
                onTitleClick = {},
                onDoubleClick = { scope.launch { connection.scrollToTop() } },
                onTabClick = { index -> scope.launch { pager.animateScrollToPage(index) } },
            )
        },
    ) { padding ->
        val contentPadding = plusContentPadding(padding)
        if (nodes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            ) {
                ArchiveMessage(
                    loading = uiState.refreshing,
                    failed = uiState.indexFailed,
                    onRetry = viewModel::refresh,
                )
            }
        } else {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pager,
                userScrollEnabled = !contentScrolling,
            ) { index ->
                val tab = remember(content.id, nodes[index], index, uiState.generation) {
                    SignalArchiveFeedsTab(
                        SignalArchiveFeedsTabInfo(content.id, content.baseUrl, index, nodes[index], uiState.generation),
                    )
                }
                CompositionLocalProvider(LocalContentPadding provides contentPadding) {
                    Column(Modifier.fillMaxSize()) {
                        if (uiState.indexFailed) {
                            ArchiveMessage(failed = true, onRetry = viewModel::refresh)
                        }
                        tab.Content(
                            active = pager.currentPage == index,
                            onIndexUpdated = viewModel::updateIndex,
                            containerRefreshing = uiState.refreshing,
                        )
                    }
                }
            }
        }
    }
}
