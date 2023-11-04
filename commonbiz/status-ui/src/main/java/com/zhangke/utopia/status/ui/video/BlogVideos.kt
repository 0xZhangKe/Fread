package com.zhangke.utopia.status.ui.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.zhangke.framework.blurhash.blurhash
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.image.decideAspect
import com.zhangke.utopia.status.ui.video.inline.InlineVideo

@Composable
fun BlogVideos(
    mediaList: List<BlogMedia>,
    hideContent: Boolean,
    onMediaClick: OnBlogMediaClick,
) {
    val videoMedia = mediaList.first()
    SingleBlogInlineVideo(
        videoMedia,
        hideContent = hideContent,
        onMediaClick = onMediaClick,
    )
}

@Composable
private fun SingleBlogInlineVideo(
    videoMedia: BlogMedia,
    hideContent: Boolean,
    onMediaClick: OnBlogMediaClick,
) {
    var modifier = Modifier.fillMaxWidth()
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
                playWhenReady = false,
                uri = videoMedia.url.toUri(),
                onPlayManually = {},
                onClick = {},
            )
        }
    }
}
