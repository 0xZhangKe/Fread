package com.zhangke.fread.feature.message

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.fread.feature.message.screens.home.NotificationsHomeScreen
import com.zhangke.fread.feature.notifications.R

class NotificationsTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(id = R.drawable.ic_notification_tab)
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
