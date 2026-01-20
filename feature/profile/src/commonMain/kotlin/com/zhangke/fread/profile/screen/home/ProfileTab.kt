package com.zhangke.fread.profile.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.ic_profile_tab
import org.jetbrains.compose.resources.painterResource

class ProfileTab() : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_profile_tab)
            return remember {
                TabOptions(
                    title = "Profile",
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        ProfileScreen()
    }
}
