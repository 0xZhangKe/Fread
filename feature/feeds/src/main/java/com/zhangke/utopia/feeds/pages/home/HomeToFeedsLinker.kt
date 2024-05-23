package com.zhangke.utopia.feeds.pages.home

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.utopia.status.model.ContentConfig

val LocalHomeToFeedLinker = staticCompositionLocalOf<HomeToFeedsLinker?> {
    null
}

class HomeToFeedsLinker(
    val onOpenDrawer: () -> Unit,
) {

    var onScrollToContentTab: ((ContentConfig) -> Unit)? = null

    fun openDrawer() {
        onOpenDrawer()
    }

    fun scrollToContentTab(contentConfig: ContentConfig) {
        onScrollToContentTab?.invoke(contentConfig)
    }
}
