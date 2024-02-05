package com.zhangke.utopia.activitypub.app.internal.screen.notifications

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.composable.notifications.StatusNotificationUi
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotification
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.common.status.model.StatusUiInteraction

class ActivityPubNotificationsScreen(
    private val userUriInsights: UserUriInsights,
) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun Screen.TabContent() {
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = getViewModel<ActivityPubNotificationsViewModel>()
            .getSubViewModel(userUriInsights)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubNotificationsContent(
            uiState = uiState,
            onTabCheckedChange = viewModel::onTabCheckedChange,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onRejectClick = viewModel::onRejectClick,
            onAcceptClick = viewModel::onAcceptClick,
            onInteractive = viewModel::onInteractive,
        )
        ConsumeSnackbarFlow(snackbarHostState, messageTextFlow = viewModel.snackMessage)
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
        onInteractive: (NotificationUiState, StatusUiInteraction) -> Unit,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NotificationTabTitle(
                uiState = uiState,
                onTabCheckedChange = onTabCheckedChange,
            )
            val state = rememberLoadableInlineVideoLazyColumnState(
                refreshing = uiState.refreshing,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
            )
            LoadableInlineVideoLazyColumn(
                state = state,
                modifier = Modifier.fillMaxSize(),
                refreshing = uiState.refreshing,
                loading = uiState.loadMoreState == LoadState.Loading,
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
                        onInteractive = { _, interaction ->
                            onInteractive(notification, interaction)
                        },
                        indexInList = index,
                        onAcceptClick = onAcceptClick,
                        onRejectClick = onRejectClick,
                    )
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
