package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.model.IdentityRole

class UserTimelineTab(
    private val tabType: UserTimelineTabType,
    private val contentCanScrollBackward: MutableState<Boolean>,
    private val role: IdentityRole,
    private val userWebFinger: WebFinger,
) : BasePagerTab() {

    override val options: PagerTabOptions
        @Composable get() {
            val title = when (tabType) {
                UserTimelineTabType.POSTS -> R.string.activity_pub_user_detail_tab_post
                UserTimelineTabType.REPLIES -> R.string.activity_pub_user_detail_tab_replies
                UserTimelineTabType.MEDIA -> R.string.activity_pub_user_detail_tab_media
            }
            return PagerTabOptions(title = stringResource(title))
        }

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val viewModel = screen.getViewModel<UserTimelineContainerViewModel>().getSubViewModel(
            tabType = tabType,
            role = role,
            webFinger = userWebFinger,
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
            nestedScrollConnection = nestedScrollConnection,
            onImmersiveEvent = {},
            onScrollInProgress = {},
        )

        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
