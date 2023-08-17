package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.zhangke.framework.utils.pxToDp
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaType
import com.zhangke.utopia.status.blog.asImageMeta

@Composable
fun BlogMedias(
    modifier: Modifier,
    mediaList: List<BlogMedia>,
) {
    if (mediaList.isEmpty()) return
    val density = LocalDensity.current
    var containerWidth: Dp? by remember {
        mutableStateOf(null)
    }
    Box(
        modifier = modifier.onGloballyPositioned {
            containerWidth = it.size.width.pxToDp(density)
        }
    ) {
        if (containerWidth != null) {
            val imageMediaList =
                mediaList.filter { it.type == BlogMediaType.IMAGE || it.type == BlogMediaType.GIFV }
            BlogImageMedias(mediaList = imageMediaList, containerWidth = containerWidth!!)
        }
    }
}

private const val defaultMediaAspect = 1F

@Composable
fun SingleBlogMedia(media: BlogMedia) {
    val meta = media.meta?.asImageMeta()
    BlogImage(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(meta?.original?.aspect ?: defaultMediaAspect),
        media,
    )
}

@Composable
fun TwoBlogImageMedia(media: BlogMedia) {
    val meta = media.meta?.asImageMeta()
    BlogImage(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(meta?.original?.aspect ?: defaultMediaAspect),
        media,
    )
}

@Composable
private fun BlogImage(modifier: Modifier, media: BlogMedia) {
    AsyncImage(
        modifier = modifier,
        model = media.url,
        contentDescription = media.description.ifEmpty { "Blog Image Media" },
    )
}
