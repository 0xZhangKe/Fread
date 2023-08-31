package com.zhangke.utopia.status.ui.image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaMeta

typealias OnBlogMediaClick = (BlogMediaClickEvent) -> Unit

data class BlogMediaClickEvent(
    val index: Int,
    val mediaList: List<BlogMedia>,
    val coordinatesList: List<LayoutCoordinates?>,
    val onDismiss: () -> Unit,
)

/**
 * Image and Gifv
 */
@Composable
fun BlogImageMedias(
    mediaList: List<BlogMedia>,
    containerWidth: Dp,
    style: BlogImageMediaStyle = BlogImageMediaDefault.defaultStyle,
    onMediaClick: OnBlogMediaClick,
) {
    val aspectList = mediaList.take(6).map { it.meta.decideAspect(style.defaultMediaAspect) }
    val mediaPosition: Array<LayoutCoordinates?> = remember {
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
                    .clickable {
                        onMediaClick(
                            BlogMediaClickEvent(
                                index = index,
                                mediaList = mediaList,
                                coordinatesList = mediaPosition.toList(),
                                onDismiss = {

                                },
                            )
                        )
                    },
                media = media,
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
internal fun BlogImage(modifier: Modifier, media: BlogMedia) {
    AsyncImage(
        modifier = modifier,
        model = media.url,
        contentScale = ContentScale.Crop,
        contentDescription = media.description.ifNullOrEmpty { "Blog Image Media" },
    )
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
