package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_user_detail_tab_media
import com.zhangke.fread.activitypub.app.activity_pub_user_detail_tab_post
import com.zhangke.fread.activitypub.app.activity_pub_user_detail_tab_replies
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.model.PlatformLocator
import org.jetbrains.compose.resources.stringResource

internal class UserTimelineTab(
    private val tabType: UserTimelineTabType,
    private val contentCanScrollBackward: MutableState<Boolean>,
    private val locator: PlatformLocator,
    private val userWebFinger: WebFinger,
    private val userId: String?,
) : BasePagerTab() {

    override val options: PagerTabOptions
        @Composable get() {
            val title = when (tabType) {
                UserTimelineTabType.POSTS -> Res.string.activity_pub_user_detail_tab_post
                UserTimelineTabType.REPLIES -> Res.string.activity_pub_user_detail_tab_replies
                UserTimelineTabType.MEDIA -> Res.string.activity_pub_user_detail_tab_media
            }
            return PagerTabOptions(title = stringResource(title))
        }

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        super.TabContent(screen, nestedScrollConnection)
        val viewModel = screen.getViewModel<UserTimelineContainerViewModel>().getSubViewModel(
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
            nestedScrollConnection = nestedScrollConnection,
            onImmersiveEvent = {},
            onScrollInProgress = {},
        )

        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
