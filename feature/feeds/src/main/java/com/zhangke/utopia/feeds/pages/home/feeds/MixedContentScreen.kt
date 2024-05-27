package com.zhangke.utopia.feeds.pages.home.feeds

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
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.utopia.commonbiz.shared.composable.FeedsContent
import com.zhangke.utopia.commonbiz.shared.feeds.CommonFeedsUiState
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
import com.zhangke.utopia.status.ui.common.ContentToolbar
import com.zhangke.utopia.status.ui.common.LocalMainTabConnection
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MixedContentScreen(
    private val configId: Long,
    private val isLatestTab: Boolean,
) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = getViewModel<MixedContentViewModel>().getSubViewModel(configId)
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
        val mainTabConnection = LocalMainTabConnection.current
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            topBar = {
                ContentToolbar(
                    title = configUiState.config?.name.orEmpty(),
                    showNextIcon = !isLatestTab,
                    scrollBehavior = scrollBehavior,
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
                    onTitleClick = onTitleClick,
                )
            },
        ) { paddings ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {
                FeedsContent(
                    uiState = uiState,
                    openScreenFlow = openScreenFlow,
                    newStatusNotifyFlow = newStatusNotifyFlow,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                    composedStatusInteraction = composedStatusInteraction,
                    nestedScrollConnection = scrollBehavior.nestedScrollConnection,
                )
            }
        }
    }
}
