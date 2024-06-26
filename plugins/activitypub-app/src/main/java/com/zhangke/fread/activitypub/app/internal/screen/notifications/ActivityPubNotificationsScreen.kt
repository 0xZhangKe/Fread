package com.zhangke.fread.activitypub.app.internal.screen.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.composable.notifications.StatusNotificationUi
import com.zhangke.fread.activitypub.app.internal.model.UserUriInsights
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusListPlaceholder

class ActivityPubNotificationsScreen(
    private val userUriInsights: UserUriInsights,
) : BasePagerTab() {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = screen.getViewModel<ActivityPubNotificationsViewModel>().getSubViewModel(userUriInsights)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubNotificationsContent(
            uiState = uiState,
            onTabCheckedChange = viewModel::onTabCheckedChange,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onRejectClick = viewModel::onRejectClick,
            onAcceptClick = viewModel::onAcceptClick,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            nestedScrollConnection = nestedScrollConnection,
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.push(it)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ActivityPubNotificationsContent(
        uiState: ActivityPubNotificationsUiState,
        onTabCheckedChange: (inMentionsTab: Boolean) -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onRejectClick: (NotificationUiState) -> Unit,
        onAcceptClick: (NotificationUiState) -> Unit,
        composedStatusInteraction: ComposedStatusInteraction,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NotificationTabTitle(
                uiState = uiState,
                onTabCheckedChange = onTabCheckedChange,
            )
            if (uiState.initializing) {
                StatusListPlaceholder()
            } else {
                val state = rememberLoadableInlineVideoLazyColumnState(
                    refreshing = uiState.refreshing,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                )
                LoadableInlineVideoLazyColumn(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .applyNestedScrollConnection(nestedScrollConnection),
                    refreshing = uiState.refreshing,
                    loadState = uiState.loadMoreState,
                    contentPadding = PaddingValues(
                        bottom = 20.dp,
                    )
                ) {
                    itemsIndexed(
                        items = uiState.dataList,
                    ) { index, notification ->
                        StatusNotificationUi(
                            modifier = Modifier.fillMaxWidth(),
                            notification = notification,
                            composedStatusInteraction = composedStatusInteraction,
                            indexInList = index,
                            onAcceptClick = onAcceptClick,
                            onRejectClick = onRejectClick,
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun NotificationTabTitle(
        uiState: ActivityPubNotificationsUiState,
        onTabCheckedChange: (inMentionsTab: Boolean) -> Unit,
    ) {
        MultiChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(0.7F),
        ) {
            SegmentedButton(
                checked = !uiState.inMentionsTab,
                onCheckedChange = {
                    onTabCheckedChange(false)
                },
                icon = {
                    SegmentedButtonDefaults.Icon(active = !uiState.inMentionsTab) {
                        Icon(
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text(text = stringResource(R.string.activity_pub_notification_tab_title_all))
            }

            SegmentedButton(
                checked = uiState.inMentionsTab,
                onCheckedChange = {
                    onTabCheckedChange(true)
                },
                icon = {
                    SegmentedButtonDefaults.Icon(active = uiState.inMentionsTab) {
                        Icon(
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(text = stringResource(R.string.activity_pub_notification_tab_title_mention))
            }
        }
    }
}
