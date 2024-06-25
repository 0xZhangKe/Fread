package com.zhangke.fread.commonbiz.shared.screen.status.context

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadErrorLineItem
import com.zhangke.framework.composable.LoadingLineItem
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.composable.onStatusMediaClick
import com.zhangke.fread.commonbiz.shared.screen.R
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.image.OnBlogMediaClick

data class StatusContextScreen(
    val role: IdentityRole,
    val status: Status,
) : BaseScreen() {

    override val key: ScreenKey
        get() = uniqueScreenKey

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val transparentNavigator = LocalTransparentNavigator.current
        val viewModel = getViewModel<StatusContextViewModel>().getSubViewModel(role, status)
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = rememberSnackbarHostState()
        StatusContextContent(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onMediaClick = { event ->
                onStatusMediaClick(
                    transparentNavigator = transparentNavigator,
                    navigator = navigator,
                    event = event,
                )
            },
            onBackClick = navigator::pop,
            composedStatusInteraction = viewModel.composedStatusInteraction,
        )
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }

    @Composable
    private fun StatusContextContent(
        uiState: StatusContextUiState,
        snackbarHostState: SnackbarHostState,
        onBackClick: () -> Unit = {},
        onMediaClick: OnBlogMediaClick,
        composedStatusInteraction: ComposedStatusInteraction,
    ) {
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
                val contextStatus = uiState.contextStatus
                if (contextStatus.isEmpty()) return@Scaffold
                val state = rememberLazyListState()
                val anchorIndex = uiState.anchorIndex
                if (anchorIndex in 0..contextStatus.lastIndex) {
                    LaunchedEffect(anchorIndex) {
                        state.animateScrollToItem(anchorIndex)
                    }
                }
                InlineVideoLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPaddings),
                    state = state,
                ) {
                    itemsIndexed(
                        items = contextStatus,
                    ) { index, statusInContext ->
                        StatusInContextUi(
                            modifier = Modifier
                                .fillMaxWidth(),
                            statusInContext = statusInContext,
                            indexInList = index,
                            onMediaClick = onMediaClick,
                            composedStatusInteraction = composedStatusInteraction,
                        )
                    }
                    if (uiState.loading) {
                        item {
                            LoadingLineItem(modifier = Modifier.fillMaxWidth())
                        }
                    } else if (uiState.errorMessage != null) {
                        item {
                            LoadErrorLineItem(
                                modifier = Modifier.fillMaxWidth(),
                                errorMessage = uiState.errorMessage,
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
        composedStatusInteraction: ComposedStatusInteraction,
    ) {
        when (statusInContext.type) {
            StatusInContextType.ANCESTOR -> AncestorBlogUi(
                modifier = modifier.clickable {
                    composedStatusInteraction.onStatusClick(statusInContext.status)
                },
                status = statusInContext.status,
                displayTime = statusInContext.status.displayTime,
                indexInList = indexInList,
                isFirst = indexInList == 0,
                onMediaClick = onMediaClick,
                composedStatusInteraction = composedStatusInteraction,
            )

            StatusInContextType.ANCHOR -> AnchorBlogUi(
                modifier = modifier,
                status = statusInContext.status,
                displayTime = statusInContext.status.displayTime,
                indexInList = indexInList,
                showUpThread = indexInList > 0,
                onMediaClick = onMediaClick,
                bottomPanelInteractions = statusInContext.status.bottomInteractions,
                moreInteractions = statusInContext.status.moreInteractions,
                composedStatusInteraction = composedStatusInteraction,
            )

            StatusInContextType.DESCENDANT -> DescendantStatusUi(
                modifier = modifier.clickable {
                    composedStatusInteraction.onStatusClick(statusInContext.status)
                },
                status = statusInContext.status,
                displayTime = statusInContext.status.displayTime,
                bottomPanelInteractions = statusInContext.status.bottomInteractions,
                moreInteractions = statusInContext.status.moreInteractions,
                indexInList = indexInList,
                onMediaClick = onMediaClick,
                composedStatusInteraction = composedStatusInteraction,
            )
        }
    }
}
