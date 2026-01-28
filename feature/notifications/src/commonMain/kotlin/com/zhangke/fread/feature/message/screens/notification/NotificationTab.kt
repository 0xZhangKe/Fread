package com.zhangke.fread.feature.message.screens.notification

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.blur.applyBlurEffect
import com.zhangke.framework.blur.blurEffectContainerColor
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.plusTopPadding
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.TabOptions
import com.zhangke.framework.utils.pxToDp
import com.zhangke.fread.commonbiz.shared.notification.StatusNotificationUi
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusListPlaceholder
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

class NotificationTab(
    private val loggedAccount: LoggedAccount,
) : BaseTab() {

    override val options: TabOptions?
        @Composable get() = null

    @Composable
    override fun Content() {
        super.Content()
        val backstack = LocalNavBackStack.currentOrThrow
        val viewModel =
            koinViewModel<NotificationContainerViewModel>().getSubViewModel(loggedAccount)
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = LocalSnackbarHostState.current
        TabPageContent(
            uiState = uiState,
            onSwitchTab = viewModel::onSwitchTab,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onAcceptClick = viewModel::onAcceptClick,
            onRejectClick = viewModel::onRejectClick,
            onNotificationShown = viewModel::onNotificationShown,
            onUnblockClick = viewModel::onUnblockClick,
            onCancelFollowRequestClick = viewModel::onCancelFollowRequestClick,
        )
        ConsumeSnackbarFlow(snackBarHostState, viewModel.errorMessageFlow)
        ConsumeFlow(viewModel.openScreenFlow) {
            backstack.add(it)
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
        onSwitchTab: (Boolean) -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onUnblockClick: (PlatformLocator, BlogAuthor) -> Unit,
        onRejectClick: (BlogAuthor) -> Unit,
        onAcceptClick: (BlogAuthor) -> Unit,
        onNotificationShown: (StatusNotificationUiState) -> Unit,
        onCancelFollowRequestClick: (PlatformLocator, BlogAuthor) -> Unit,
    ) {
        var tabTitleHeight: Dp by remember { mutableStateOf(20.dp) }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NotificationTabTitle(
                uiState = uiState,
                onTabCheckedChange = onSwitchTab,
                onHeightChanged = { tabTitleHeight = it },
            )
            if (uiState.initializing) {
                StatusListPlaceholder()
            } else {
                CompositionLocalProvider(
                    LocalContentPadding provides plusTopPadding(tabTitleHeight)
                ) {
                    val state = rememberLoadableInlineVideoLazyColumnState(
                        refreshing = uiState.refreshing,
                        onRefresh = onRefresh,
                        onLoadMore = onLoadMore,
                    )
                    LoadableInlineVideoLazyColumn(
                        state = state,
                        modifier = Modifier
                            .fillMaxSize(),
                        refreshing = uiState.refreshing,
                        loadState = uiState.loadMoreState,
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
                                onUnblockClick = onUnblockClick,
                                onCancelFollowRequestClick = onCancelFollowRequestClick,
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
    }

    @Composable
    private fun NotificationTabTitle(
        uiState: NotificationUiState,
        onTabCheckedChange: (inMentionsTab: Boolean) -> Unit,
        onHeightChanged: (Dp) -> Unit,
    ) {
        val containerColor = MaterialTheme.colorScheme.surface
        val density = LocalDensity.current
        Box(
            modifier = Modifier.fillMaxWidth()
                .onSizeChanged { onHeightChanged(it.height.pxToDp(density)) }
                .background(blurEffectContainerColor(containerColor = containerColor))
                .applyBlurEffect(containerColor = containerColor),
            contentAlignment = Alignment.Center,
        ) {
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(0.7F),
            ) {
                SegmentedButton(
                    checked = !uiState.inOnlyMentionTab,
                    onCheckedChange = { onTabCheckedChange(false) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                ) {
                    Text(text = stringResource(LocalizedString.notificationsTabAll))
                }
                SegmentedButton(
                    checked = uiState.inOnlyMentionTab,
                    onCheckedChange = { onTabCheckedChange(true) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                ) {
                    Text(text = stringResource(LocalizedString.notificationsTabMention))
                }
            }
        }
    }
}
