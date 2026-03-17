package com.zhangke.fread.commonbiz.shared.composable

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerImage
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreenNavKey
import com.zhangke.fread.commonbiz.shared.screen.video.FullVideoScreenNavKey
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.blog.asImageMetaOrNull
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent
import com.zhangke.fread.status.ui.image.ClickedBlogMedia

fun onStatusMediaClick(
    navigator: NavBackStack<NavKey>,
    event: BlogMediaClickEvent,
) {
    when (event) {
        is BlogMediaClickEvent.BlogImageClickEvent -> {
            if (event.mediaList[event.index].media.type == BlogMediaType.GIFV) {
                navigator.add(FullVideoScreenNavKey(event.mediaList[event.index].media.url))
                return
            }
            navigator.add(
                ImageViewerScreenNavKey(
                    imageList = event.mediaList.toImages(),
                    selectedIndex = event.index,
                )
            )
        }

        is BlogMediaClickEvent.BlogVideoClickEvent -> {
            navigator.add(FullVideoScreenNavKey(event.media.url))
        }
    }
}

fun ClickedBlogMedia.toImage(): ImageViewerImage {
    val media = this.media
    return ImageViewerImage(
        url = media.url,
        previewUrl = media.previewUrl,
        description = media.description,
        blurhash = media.blurhash,
        aspect = media.meta?.asImageMetaOrNull()?.original?.aspect,
        sharedElementKey = this.sharedElementKey,
    )
}

fun List<ClickedBlogMedia>.toImages(): List<ImageViewerImage> {
    return this.map { it.toImage() }
}
