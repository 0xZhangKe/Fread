package com.zhangke.fread.feature.message.screens.notification

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
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
import com.zhangke.framework.voyager.AnimatedScreenContentScope
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.commonbiz.shared.notification.StatusNotificationUi
import com.zhangke.fread.feature.notifications.Res
import com.zhangke.fread.feature.notifications.notifications_tab_all
import com.zhangke.fread.feature.notifications.notifications_tab_mention
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusListPlaceholder
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

class NotificationTab(
    private val loggedAccount: LoggedAccount,
) : BasePagerTab() {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?,
        animatedScreenContentScope: AnimatedScreenContentScope?,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            screen.getViewModel<NotificationContainerViewModel>().getSubViewModel(loggedAccount)
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = LocalSnackbarHostState.current
        TabPageContent(
            uiState = uiState,
            onSwitchTab = viewModel::onSwitchTab,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            nestedScrollConnection = nestedScrollConnection,
            onAcceptClick = viewModel::onAcceptClick,
            onRejectClick = viewModel::onRejectClick,
            onNotificationShown = viewModel::onNotificationShown,
        )
        ConsumeSnackbarFlow(snackBarHostState, viewModel.errorMessageFlow)
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.push(it)
        }
        if (uiState.dataList.isNotEmpty()) {
            val first = uiState.dataList.first()
            LaunchedEffect(first.id, first.fromLocal) {
                // 停留1秒表示已读
                delay(1000)
                viewModel.onPageResume()
            }
        }
    }

    @Composable
    private fun TabPageContent(
        uiState: NotificationUiState,
        composedStatusInteraction: ComposedStatusInteraction,
        nestedScrollConnection: NestedScrollConnection?,
        onSwitchTab: (Boolean) -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onRejectClick: (StatusNotification.FollowRequest) -> Unit,
        onAcceptClick: (StatusNotification.FollowRequest) -> Unit,
        onNotificationShown: (StatusNotificationUiState) -> Unit,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NotificationTabTitle(
                uiState = uiState,
                onTabCheckedChange = onSwitchTab,
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
                        val backgroundColor by animateColorAsState(
                            if (notification.unreadState) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2F)
                            } else {
                                Color.Transparent
                            }
                        )
                        StatusNotificationUi(
                            modifier = Modifier.fillMaxWidth()
                                .background(backgroundColor),
                            notification = notification.notification,
                            composedStatusInteraction = composedStatusInteraction,
                            indexInList = index,
                            onAcceptClick = onAcceptClick,
                            onRejectClick = onRejectClick,
                        )
                        if (notification.unreadState) {
                            LaunchedEffect(notification) {
                                delay(1000)
                                onNotificationShown(notification)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NotificationTabTitle(
        uiState: NotificationUiState,
        onTabCheckedChange: (inMentionsTab: Boolean) -> Unit,
    ) {
        MultiChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(0.7F),
        ) {
            SegmentedButton(
                checked = !uiState.inOnlyMentionTab,
                onCheckedChange = {
                    onTabCheckedChange(false)
                },
                icon = {
                    SegmentedButtonDefaults.Icon(active = !uiState.inOnlyMentionTab) {
                        Icon(
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text(text = stringResource(Res.string.notifications_tab_all))
            }

            SegmentedButton(
                checked = uiState.inOnlyMentionTab,
                onCheckedChange = {
                    onTabCheckedChange(true)
                },
                icon = {
                    SegmentedButtonDefaults.Icon(active = uiState.inOnlyMentionTab) {
                        Icon(
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(text = stringResource(Res.string.notifications_tab_mention))
            }
        }
    }
}
