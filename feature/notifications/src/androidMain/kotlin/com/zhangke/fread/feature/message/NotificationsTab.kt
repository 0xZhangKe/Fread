package com.zhangke.fread.feature.message

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.fread.feature.message.screens.home.NotificationsHomeScreen
import com.zhangke.fread.feature.notifications.R
import com.zhangke.fread.feature.notifications.Res
import com.zhangke.fread.feature.notifications.ic_notification_tab
import org.jetbrains.compose.resources.painterResource

class NotificationsTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_notification_tab)
            return remember {
                TabOptions(
                    index = tabIndex, title = "Notifications", icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(NotificationsHomeScreen())
    }
}
