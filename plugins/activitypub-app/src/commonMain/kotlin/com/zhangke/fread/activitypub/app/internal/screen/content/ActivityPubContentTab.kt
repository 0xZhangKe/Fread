package com.zhangke.fread.activitypub.app.internal.screen.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.common.page.BasePagerTab

class ActivityPubContentTab: BasePagerTab() {

    override val options: PagerTabOptions?
        @Composable get() = TODO("Not yet implemented")

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
    }
}