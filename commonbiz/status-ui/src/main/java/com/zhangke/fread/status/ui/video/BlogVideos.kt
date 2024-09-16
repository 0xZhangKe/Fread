package com.zhangke.fread.status.ui.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zhangke.framework.blurhash.blurhash
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.image.decideAspect
import com.zhangke.fread.status.ui.video.inline.InlineVideo

@Composable
fun BlogVideos(
    mediaList: List<BlogMedia>,
    hideContent: Boolean,
    indexInList: Int,
    onMediaClick: OnBlogMediaClick,
) {
    val videoMedia = mediaList.first()
    SingleBlogInlineVideo(
        videoMedia = videoMedia,
        hideContent = hideContent,
        indexInList = indexInList,
        onMediaClick = onMediaClick,
    )
}

@Composable
private fun SingleBlogInlineVideo(
    videoMedia: BlogMedia,
    hideContent: Boolean,
    indexInList: Int,
    onMediaClick: OnBlogMediaClick,
) {
    var modifier = Modifier.clip(RoundedCornerShape(8.dp)).fillMaxWidth()
    if (videoMedia.blurhash.isNullOrEmpty().not()) {
        modifier = modifier.blurhash(videoMedia.blurhash!!)
    }
    val aspect = videoMedia.meta.decideAspect(1.78F)
    modifier = modifier.aspectRatio(aspect)
    Box(
        modifier = modifier
    ) {
        if (!hideContent) {
            InlineVideo(
                aspectRatio = aspect,
                coverImage = videoMedia.previewUrl,
                indexInList = indexInList,
                uri = remember(videoMedia.url) {
                    videoMedia.url.toPlatformUri()
                },
                onClick = {
                    onMediaClick(
                        BlogMediaClickEvent.BlogVideoClickEvent(
                            index = indexInList,
                            media = videoMedia,
                        )
                    )
                },
            )
        }
    }
}
