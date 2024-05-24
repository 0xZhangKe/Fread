package com.zhangke.utopia.commonbiz.shared.composable

import androidx.core.net.toUri
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.utopia.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.utopia.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.utopia.commonbiz.shared.screen.toImages
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent

fun onStatusMediaClick(
    transparentNavigator: TransparentNavigator,
    navigator: Navigator,
    event: BlogMediaClickEvent,
){
    when (event) {
        is BlogMediaClickEvent.BlogImageClickEvent -> {
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
