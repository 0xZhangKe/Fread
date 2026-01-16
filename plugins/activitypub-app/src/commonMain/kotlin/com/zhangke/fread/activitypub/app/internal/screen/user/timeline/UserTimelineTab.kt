package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

internal class UserTimelineTab(
    private val tabType: UserTimelineTabType,
    private val contentCanScrollBackward: MutableState<Boolean>,
    private val locator: PlatformLocator,
    private val userWebFinger: WebFinger,
    private val userId: String?,
) : BaseTab() {

    override val options: TabOptions
        @Composable get() {
            val title = when (tabType) {
                UserTimelineTabType.POSTS -> LocalizedString.activity_pub_user_detail_tab_post
                UserTimelineTabType.REPLIES -> LocalizedString.activity_pub_user_detail_tab_replies
                UserTimelineTabType.MEDIA -> LocalizedString.activity_pub_user_detail_tab_media
            }
            return TabOptions(title = stringResource(title))
        }

    @Composable
    override fun Content() {
        super.Content()
        val viewModel = koinViewModel<UserTimelineContainerViewModel>().getSubViewModel(
            tabType = tabType,
            locator = locator,
            webFinger = userWebFinger,
            userId = userId,
        )
        val uiState by viewModel.uiState.collectAsState()

        FeedsContent(
            uiState = uiState,
            openScreenFlow = viewModel.openScreenFlow,
            newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            observeScrollToTopEvent = true,
            contentCanScrollBackward = contentCanScrollBackward,
            nestedScrollConnection = null,
            onImmersiveEvent = {},
            onScrollInProgress = {},
        )

        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
