package com.zhangke.fread.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.fread.profile.screen.home.ProfileHomePage

class ProfileTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(id = R.drawable.ic_profile_tab)
            return remember {
                TabOptions(
                    index = tabIndex, title = "Profile", icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(ProfileHomePage())
    }
}
