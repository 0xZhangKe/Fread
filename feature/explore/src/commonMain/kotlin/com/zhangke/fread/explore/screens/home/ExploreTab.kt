package com.zhangke.fread.explore.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.ic_explorer
import org.jetbrains.compose.resources.painterResource

class ExploreTab() : BaseTab() {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_explorer)
            return remember {
                TabOptions(
                    title = "Explore",
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        super.Content()
        ExplorerScreen()
    }
}
