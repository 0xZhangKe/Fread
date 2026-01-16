package com.zhangke.fread.feature.message.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.feature.notifications.Res
import com.zhangke.fread.feature.notifications.ic_notification_tab
import org.jetbrains.compose.resources.painterResource

class NotificationsTab() : BaseTab() {

    override val options: TabOptions?
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_notification_tab)
            return remember {
                TabOptions(
                    title = "Notifications",
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        NotificationScreen()
    }
}
