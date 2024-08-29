package com.zhangke.fread.status.ui.image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.seiko.imageloader.imageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.option.SizeResolver
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.blurhash.blurhash
import com.zhangke.framework.composable.video.VideoPlayer
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaMeta
import com.zhangke.fread.status.blog.BlogMediaType

typealias OnBlogMediaClick = (BlogMediaClickEvent) -> Unit

sealed interface BlogMediaClickEvent {

    data class BlogImageClickEvent(
        val index: Int,
        val mediaList: List<BlogMedia>,
        val coordinatesList: List<LayoutCoordinates?>,
    ) : BlogMediaClickEvent

    data class BlogVideoClickEvent(
        val index: Int,
        val media: BlogMedia,
    ) : BlogMediaClickEvent
}

/**
 * Image and Gifv
 */
@Composable
fun BlogImageMedias(
    mediaList: List<BlogMedia>,
    containerWidth: Dp,
    hideContent: Boolean,
    style: BlogImageMediaStyle = BlogImageMediaDefault.defaultStyle,
    onMediaClick: OnBlogMediaClick,
) {
    val aspectList = mediaList.take(6).map { it.meta.decideAspect(style.defaultMediaAspect) }
    val mediaPosition: Array<LayoutCoordinates?> = remember(mediaList) {
        arrayOfNulls(mediaList.size)
    }
    BlogImageLayout(
        modifier = Modifier.clip(RoundedCornerShape(style.radius)),
        containerWidth = containerWidth,
        aspectList = aspectList,
        style = style,
        itemContent = { index ->
            val media = mediaList[index]
            BlogImage(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        mediaPosition[index] = it
                    }
                    .clickable(enabled = !hideContent) {
                        onMediaClick(
                            BlogMediaClickEvent.BlogImageClickEvent(
                                index = index,
                                mediaList = mediaList,
                                coordinatesList = mediaPosition.toList(),
                            )
                        )
                    },
                media = media,
                hideContent = hideContent,
            )
        }
    )
}

/**
 * For Image or gif
 */
@Composable
internal fun BlogImageLayout(
    modifier: Modifier,
    containerWidth: Dp,
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    when (aspectList.size) {
        1 -> SingleBlogImageLayout(
            modifier = modifier,
            style = style,
            aspect = aspectList.first(),
            itemContent = { itemContent(0) },
        )

        2 -> DoubleBlogImageLayout(
            modifier = modifier,
            style = style,
            aspectList = aspectList,
            itemContent = itemContent,
        )

        3 -> TripleImageMediaLayout(
            modifier = modifier,
            containerWidth = containerWidth,
            style = style,
            aspectList = aspectList,
            itemContent = itemContent,
        )

        4 -> QuadrupleImageMediaLayout(
            modifier = modifier,
            containerWidth = containerWidth,
            aspectList = aspectList,
            style = style,
            itemContent = itemContent,
        )

        5 -> FivefoldImageMediaLayout(
            modifier = modifier,
            containerWidth = containerWidth,
            aspectList = aspectList,
            style = style,
            itemContent = itemContent,
        )

        6 -> SixfoldImageMediaLayout(
            modifier = modifier,
            aspectList = aspectList,
            style = style,
            itemContent = itemContent,
        )
    }
}

@Composable
internal fun BlogImage(
    modifier: Modifier,
    media: BlogMedia,
    hideContent: Boolean,
) {
    val context = LocalContext.current
    if (hideContent) {
        LaunchedEffect(media) {
            context.imageLoader.execute(
                ImageRequest {
                    data(media.url)
                    size(SizeResolver(Size(50f, 50f)))
                }
            )
        }
    }
    val mediaModifier = modifier.run {
        if (media.blurhash.isNullOrEmpty().not()) {
            blurhash(media.blurhash!!)
        } else {
            this
        }
    }
    if (media.type == BlogMediaType.GIFV) {
        Box(modifier = mediaModifier) {
            AutoSizeImage(
                remember {
                    ImageRequest {
                        if (!hideContent) {
                            data(media.url)
                        }
                    }
                },
                modifier = mediaModifier,
                contentScale = ContentScale.Crop,
                contentDescription = media.description.ifNullOrEmpty { "Blog Image Media" },
            )

            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center),
                imageVector = Icons.Default.PlayCircleOutline,
                tint = Color.White,
                contentDescription = "Play",
            )
        }
    } else {
        AutoSizeImage(
            remember {
                ImageRequest {
                    if (!hideContent) {
                        data(media.url)
                    }
                }
            },
            modifier = mediaModifier,
            contentScale = ContentScale.Crop,
            contentDescription = media.description.ifNullOrEmpty { "Blog Image Media" },
        )
    }
}

@Composable
private fun BlogGifVideoMedia(
    modifier: Modifier,
    media: BlogMedia,
    hideContent: Boolean,
) {
    val videoUri = remember(media) {
        media.url.toUri()
    }
    Box(
        modifier = modifier,
    ) {
        if (!hideContent) {
            VideoPlayer(
                modifier = Modifier.fillMaxSize(),
                uri = videoUri,
                playWhenReady = true,
            )
        }
    }
}

@Composable
internal fun VerticalSpacer(height: Dp) {
    Spacer(
        modifier = Modifier
            .width(1.dp)
            .height(height)
    )
}

@Composable
internal fun HorizontalSpacer(width: Dp) {
    Spacer(
        modifier = Modifier
            .width(width)
            .height(1.dp)
    )
}

internal fun BlogMediaMeta?.decideAspect(defaultMediaAspect: Float): Float {
    val metaAspect = when (this) {
        is BlogMediaMeta.ImageMeta -> this.original?.aspect
        is BlogMediaMeta.GifvMeta -> this.aspect ?: this.original?.aspect
        is BlogMediaMeta.VideoMeta -> this.aspect ?: this.original?.aspect
        else -> null
    }
    return metaAspect ?: defaultMediaAspect
}

data class BlogImageMediaStyle(
    val radius: Dp,
    val horizontalDivider: Dp,
    val verticalDivider: Dp,
    val defaultMediaAspect: Float,
    val minAspect: Float,
    val maxAspect: Float,
    val maxWeightInHorizontal: Float,
    val minWeightInHorizontal: Float,
    val maxWeightInHorizontalThreshold: Float,
    val quadrupleHorizontalThreshold: Float,
    val quadrupleVerticalThreshold: Float,
    val sixfoldAspect: Float,
)

object BlogImageMediaDefault {

    val defaultStyle = BlogImageMediaStyle(
        radius = 8.dp,
        horizontalDivider = 4.dp,
        verticalDivider = 4.dp,
        defaultMediaAspect = 1F,
        minAspect = 0.6F,
        maxAspect = 3F,
        maxWeightInHorizontal = 0.75F,
        minWeightInHorizontal = 0.5F,
        maxWeightInHorizontalThreshold = 0.6F,
        quadrupleHorizontalThreshold = 0.67F,
        quadrupleVerticalThreshold = 1.5F,
        sixfoldAspect = 1.5F,
    )
}

internal fun BlogImageMediaStyle.getCompliantAspect(aspect: Float): Float {
    return aspect.coerceAtLeast(minAspect).coerceAtMost(maxAspect)
}

/**
 * Image more than two, and is horizontal arrange.
 */
internal fun BlogImageMediaStyle.decideFirstImageWeightInHorizontalMode(aspect: Float): Float {
    if (aspect >= 1F) {
        return minWeightInHorizontal
    }
    if (aspect <= maxWeightInHorizontalThreshold) return maxWeightInHorizontal
    val floatingWeight = maxWeightInHorizontal - minWeightInHorizontal
    val factor = 1F / aspect - 1F
    val weight = minWeightInHorizontal + floatingWeight * factor
    return weight.coerceAtLeast(minWeightInHorizontal).coerceAtMost(maxWeightInHorizontal)
}
