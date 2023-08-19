package com.zhangke.utopia.status.ui.image

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaMeta

/**
 * Image and Gifv
 */
@Composable
fun BlogImageMedias(
    mediaList: List<BlogMedia>,
    containerWidth: Dp,
    style: BlogImageMediaStyle = BlogImageMediaDefault.defaultStyle,
) {
    val aspectList = mediaList.map { it.meta.decideAspect(style.defaultMediaAspect) }
    BlogImageLayout(
        containerWidth = containerWidth,
        aspectList = aspectList,
        style = style,
        itemContent = { index ->
            val media = mediaList[index]
            BlogImage(
                modifier = Modifier.fillMaxSize(),
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
    containerWidth: Dp,
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    when (aspectList.size) {
        1 -> SingleBlogImageLayout(
            style = style,
            aspect = aspectList.first(),
            itemContent = { itemContent(0) },
        )

        2 -> DoubleBlogImageLayout(
            style = style,
            aspectList = aspectList,
            itemContent = itemContent,
        )

        3 -> TripleImageMediaLayout(
            containerWidth = containerWidth,
            style = style,
            aspectList = aspectList,
            itemContent = itemContent,
        )

        4 -> QuadrupleImageMediaLayout(
            containerWidth = containerWidth,
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
        contentDescription = media.description.ifEmpty { "Blog Image Media" },
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
)

object BlogImageMediaDefault {

    val defaultStyle = BlogImageMediaStyle(
        radius = 6.dp,
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
