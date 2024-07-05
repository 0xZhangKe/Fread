package com.zhangke.fread.feeds.pages.home.feeds

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.analytics.HomeTabElements
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.commonbiz.shared.feeds.CommonFeedsUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.common.ContentToolbar
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MixedContentScreen(
    private val configId: Long,
    private val isLatestTab: Boolean,
) : BasePagerTab() {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = screen.getViewModel<MixedContentViewModel>().getSubViewModel(configId)
        val uiState by viewModel.uiState.collectAsState()
        val configUiState by viewModel.configUiState.collectAsState()
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        MixedContentUi(
            uiState = uiState,
            configUiState = configUiState,
            openScreenFlow = viewModel.openScreenFlow,
            newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onTitleClick = viewModel::onContentTitleClick,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MixedContentUi(
        uiState: CommonFeedsUiState,
        configUiState: MixedContentUiState,
        openScreenFlow: SharedFlow<Screen>,
        newStatusNotifyFlow: SharedFlow<Unit>,
        onTitleClick: () -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        composedStatusInteraction: ComposedStatusInteraction,
    ) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        val mainTabConnection = LocalNestedTabConnection.current
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            topBar = {
                ContentToolbar(
                    title = configUiState.config?.name.orEmpty(),
                    showNextIcon = !isLatestTab,
                    scrollBehavior = scrollBehavior,
                    onMenuClick = {
                        reportClick(HomeTabElements.SHOW_DRAWER)
                        coroutineScope.launch {
                            mainTabConnection.openDrawer()
                        }
                    },
                    onNextClick = {
                        reportClick(HomeTabElements.NEXT)
                        coroutineScope.launch {
                            mainTabConnection.switchToNextTab()
                        }
                    },
                    onTitleClick = {
                        reportClick(HomeTabElements.TITLE)
                        onTitleClick()
                    },
                    onDoubleClick = {
                        reportClick(HomeTabElements.TITLE_DOUBLE_CLICK)
                        coroutineScope.launch {
                            mainTabConnection.scrollToTop()
                        }
                    }
                )
            },
        ) { paddings ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {
                val nestedTabConnection = LocalNestedTabConnection.current
                FeedsContent(
                    uiState = uiState,
                    openScreenFlow = openScreenFlow,
                    newStatusNotifyFlow = newStatusNotifyFlow,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                    composedStatusInteraction = composedStatusInteraction,
                    observeScrollToTopEvent = true,
                    nestedScrollConnection = scrollBehavior.nestedScrollConnection,
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
