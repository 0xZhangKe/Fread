package com.zhangke.fread.commonbiz.shared.composable

import androidx.core.net.toUri
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.fread.commonbiz.shared.screen.toImages
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent

fun onStatusMediaClick(
    transparentNavigator: TransparentNavigator,
    navigator: Navigator,
    event: BlogMediaClickEvent,
){
    when (event) {
        is BlogMediaClickEvent.BlogImageClickEvent -> {
            if (event.mediaList[event.index].type == BlogMediaType.GIFV) {
                navigator.push(FullVideoScreen(event.mediaList[event.index].url.toUri()))
                return
            }
            transparentNavigator.push(
                ImageViewerScreen(
                    imageList = event.mediaList.toImages(),
                    selectedIndex = event.index,
                    coordinatesList = event.coordinatesList,
                )
            )
        }

        is BlogMediaClickEvent.BlogVideoClickEvent -> {
            navigator.push(FullVideoScreen(event.media.url.toUri()))
        }
    }
}
