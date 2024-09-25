package com.zhangke.fread.activitypub.app.internal.screen.user.status

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_bookmarks_list_title
import com.zhangke.fread.activitypub.app.activity_pub_favourites_list_title
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.RouteParam
import org.jetbrains.compose.resources.stringResource

@Destination(StatusListScreenRoute.ROUTE)
class StatusListScreen(
    @RouteParam(StatusListScreenRoute.PARAM_ROLE) private val roleString: String? = null,
    @RouteParam(StatusListScreenRoute.PARAM_TYPE) private val typeString: String? = null,
    private val role: IdentityRole? = null,
    private val type: StatusListType? = null,
) : BaseScreen() {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<StatusListViewModel, StatusListViewModel.Factory> {
            if (roleString.isNullOrEmpty() || typeString.isNullOrEmpty()) {
                it.create(role!!, type!!)
            } else {
                val role = IdentityRole.decodeFromString(roleString)!!
                val type = StatusListType.valueOf(roleString)
                it.create(role, type)
            }
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()

        Scaffold(
            topBar = {
                Toolbar(
                    title = viewModel.type.pageTitle,
                    onBackClick = navigator::pop,
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                FeedsContent(
                    uiState = uiState,
                    openScreenFlow = viewModel.openScreenFlow,
                    newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
                    onRefresh = viewModel::onRefresh,
                    onLoadMore = viewModel::onLoadMore,
                    composedStatusInteraction = viewModel.composedStatusInteraction,
                    observeScrollToTopEvent = true,
                    onImmersiveEvent = {},
                    onScrollInProgress = {},
                )
            }
        }
        ConsumeSnackbarFlow(
            hostState = snackBarHostState,
            messageTextFlow = viewModel.errorMessageFlow,
        )
    }

    private val StatusListType.pageTitle: String
        @Composable get() = when (this) {
            StatusListType.FAVOURITES -> stringResource(Res.string.activity_pub_favourites_list_title)
            StatusListType.BOOKMARKS -> stringResource(Res.string.activity_pub_bookmarks_list_title)
        }
}
