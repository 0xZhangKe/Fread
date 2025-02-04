package com.zhangke.fread.feature.message.screens.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.status.account.LoggedAccount

class NotificationTab(
    val loggedAccount: LoggedAccount,
) : BasePagerTab() {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?
    ) {
        val viewModel =
            screen.getViewModel<NotificationContainerViewModel>().getSubViewModel(loggedAccount)

    }
}
