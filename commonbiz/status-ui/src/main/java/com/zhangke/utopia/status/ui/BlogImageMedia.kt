package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zhangke.framework.ktx.second
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaMeta

/**
 * Image and Gifv
 */
@Composable
fun BoxScope.BlogImageMedias(
    mediaList: List<BlogMedia>,
    containerWidth: Dp,
) {
    val aspectList = mediaList.map { it.meta.decideAspect() }
    BlogImageLayout(
        containerWidth = containerWidth,
        aspectList = aspectList,
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
fun BlogImageLayout(
    containerWidth: Dp,
    aspectList: List<Float>,
    itemContent: @Composable (index: Int) -> Unit,
) {
    when (aspectList.size) {
        1 -> SingleBlogImageLayout(
            aspect = aspectList.first(),
            itemContent = { itemContent(0) },
        )

        2 -> DoubleBlogImageLayout(
            containerWidth = containerWidth,
            aspectList = aspectList,
            itemContent = itemContent,
        )
    }
}

@Composable
private fun SingleBlogImageLayout(
    aspect: Float,
    itemContent: @Composable () -> Unit,
) {
    val minAspect = 0.58F
    val maxAspect = 3F
    val fixedAspect = aspect.coerceAtLeast(minAspect).coerceAtMost(maxAspect)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(fixedAspect)
            .clip(RoundedCornerShape(BlogImageMediaDefault.radius)),
        contentAlignment = Alignment.Center,
    ) {
        itemContent()
    }
}

@Composable
private fun DoubleBlogImageLayout(
    containerWidth: Dp,
    aspectList: List<Float>,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val minAspect = 0.58F
    val maxAspect = 3F
    val remainderWidth = containerWidth - BlogImageMediaDefault.horizontalDivider
    val firstAspect = aspectList.first()
    val secondAspect = aspectList.second()
    val firstFixedAspect = firstAspect.coerceAtLeast(minAspect).coerceAtMost(maxAspect)
    val secondFixedAspect = secondAspect.coerceAtLeast(minAspect).coerceAtMost(maxAspect)
    if (firstAspect > 1 && secondAspect > 1) {
        // vertical arrange
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(BlogImageMediaDefault.radius))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(firstFixedAspect)
            ) {
                itemContent(0)
            }
            VerticalSpacer()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(secondFixedAspect)
            ) {
                itemContent(1)
            }
        }
    } else {
        // horizontal arrange
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(BlogImageMediaDefault.radius))
        ) {
            Box(
                modifier = Modifier
                    .weight(firstFixedAspect)
                    .aspectRatio(firstFixedAspect)
            ) {
                itemContent(0)
            }
            HorizontalSpacer()
            Box(
                modifier = Modifier
                    .weight(secondFixedAspect)
                    .aspectRatio(secondFixedAspect)
            ) {
                itemContent(1)
            }
        }
    }
}

@Composable
private fun VerticalSpacer() {
    Spacer(
        modifier = Modifier
            .width(1.dp)
            .height(BlogImageMediaDefault.verticalDivider)
    )
}

@Composable
private fun HorizontalSpacer() {
    Spacer(
        modifier = Modifier
            .width(BlogImageMediaDefault.horizontalDivider)
            .height(1.dp)
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

private fun BlogMediaMeta?.decideAspect(): Float {
    val metaAspect = when (this) {
        is BlogMediaMeta.ImageMeta -> this.original?.aspect
        is BlogMediaMeta.GifvMeta -> this.aspect ?: this.original?.aspect
        else -> null
    }
    return metaAspect ?: BlogImageMediaDefault.defaultMediaAspect
}

object BlogImageMediaDefault {

    val radius = 2.dp

    val horizontalDivider = 4.dp

    val verticalDivider = 4.dp

    const val defaultMediaAspect = 1F

}
