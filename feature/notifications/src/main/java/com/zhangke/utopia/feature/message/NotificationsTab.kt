package com.zhangke.utopia.feature.message

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.utopia.feature.message.screens.home.NotificationsHomeScreen

class NotificationsTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(Icons.Default.Notifications)
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
