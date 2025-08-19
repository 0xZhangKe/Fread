package com.zhangke.fread.profile.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.ic_profile_tab
import org.jetbrains.compose.resources.painterResource

class ProfileTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_profile_tab)
            return remember {
                TabOptions(
                    title = "Profile",
                    icon = icon,
                    index = tabIndex,
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(ProfileScreen())
    }
}
