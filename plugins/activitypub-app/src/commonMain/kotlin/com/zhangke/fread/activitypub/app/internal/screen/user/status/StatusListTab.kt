package com.zhangke.fread.activitypub.app.internal.screen.user.status

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_bookmarks
import com.zhangke.fread.statusui.status_ui_likes
import org.jetbrains.compose.resources.stringResource

class StatusListTabStatusListScreen(
    private val locator: PlatformLocator,
    private val type: StatusListType,
    private val contentCanScrollBackward: MutableState<Boolean>?,
) : BasePagerTab() {

    override val options: PagerTabOptions?
        @Composable
        get() = PagerTabOptions(
            title = when (type) {
                StatusListType.BOOKMARKS -> stringResource(Res.string.status_ui_bookmarks)
                StatusListType.FAVOURITES -> stringResource(Res.string.status_ui_likes)
            },
        )

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?
    ) {
        val viewModel =
            screen.getViewModel<StatusListContainerViewModel>().getViewModel(locator, type)
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = LocalSnackbarHostState.current

        Box(modifier = Modifier.fillMaxSize()) {
            FeedsContent(
                uiState = uiState,
                openScreenFlow = viewModel.openScreenFlow,
                newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
                onRefresh = viewModel::onRefresh,
                onLoadMore = viewModel::onLoadMore,
                contentCanScrollBackward = contentCanScrollBackward,
                composedStatusInteraction = viewModel.composedStatusInteraction,
                observeScrollToTopEvent = true,
                onImmersiveEvent = {},
                onScrollInProgress = {},
            )
        }

        ConsumeSnackbarFlow(
            hostState = snackBarHostState,
            messageTextFlow = viewModel.errorMessageFlow,
        )
    }
}
