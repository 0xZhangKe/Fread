package com.zhangke.fread.commonbiz.shared.composable

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.zhangke.fread.commonbiz.shared.screen.FullVideoScreenNavKey
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreenNavKey
import com.zhangke.fread.commonbiz.shared.screen.toImages
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent

actual fun onStatusMediaClick(
    navigator: NavBackStack<NavKey>,
    event: BlogMediaClickEvent,
) {
    when (event) {
        is BlogMediaClickEvent.BlogImageClickEvent -> {
            if (event.mediaList[event.index].type == BlogMediaType.GIFV) {
                navigator.add(FullVideoScreenNavKey(event.mediaList[event.index].url))
                return
            }
            navigator.add(
                ImageViewerScreenNavKey(
                    imageList = event.mediaList.toImages(),
                    sharedElementLabel = event.sharedElementLabel,
                    selectedIndex = event.index,
                )
            )
        }

        is BlogMediaClickEvent.BlogVideoClickEvent -> {
            navigator.add(FullVideoScreenNavKey(event.media.url))
        }
    }
}
