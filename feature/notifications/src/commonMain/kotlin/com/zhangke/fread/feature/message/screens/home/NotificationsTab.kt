package com.zhangke.fread.feature.message.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.common.notification.NotificationUnreadCounter
import com.zhangke.fread.feature.notifications.Res
import com.zhangke.fread.feature.notifications.ic_notification_tab
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

class NotificationsTab : BaseTab() {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_notification_tab)
            val unreadCounter: NotificationUnreadCounter = koinInject()
            return remember(icon, unreadCounter) {
                TabOptions(
                    title = "Notifications",
                    icon = icon,
                    unreadCountFlow = unreadCounter.totalUnreadFlow,
                )
            }
        }

    @Composable
    override fun Content() {
        NotificationScreen()
    }
}
