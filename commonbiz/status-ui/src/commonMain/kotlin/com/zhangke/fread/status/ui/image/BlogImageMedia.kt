package com.zhangke.fread.status.ui.image

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.blurhash.blurhash
import com.zhangke.framework.imageloader.executeSafety
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.voyager.AnimatedScreenContentScope
import com.zhangke.framework.voyager.sharedBoundsBetweenScreen
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
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BlogImageMedias(
    mediaList: List<BlogMedia>,
    containerWidth: Dp,
    hideContent: Boolean,
    style: BlogImageMediaStyle = BlogImageMediaDefault.defaultStyle,
    onMediaClick: OnBlogMediaClick,
    showAlt: Boolean = true,
    animatedScreenContentScope: AnimatedScreenContentScope? = null,
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
                showAlt = showAlt,
                animatedScreenContentScope = animatedScreenContentScope,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun BlogImage(
    modifier: Modifier,
    media: BlogMedia,
    hideContent: Boolean,
    showAlt: Boolean,
    animatedScreenContentScope: AnimatedScreenContentScope? = null,
) {
    val imageUrl = if (media.type == BlogMediaType.GIFV) media.previewUrl else media.url
    if (hideContent) {
        val imageLoader = LocalImageLoader.current
        LaunchedEffect(media) {
            imageLoader.executeSafety(
                ImageRequest {
                    data(imageUrl)
                }
            )
        }
    }

    Box(modifier = modifier.blurhash(media.blurhash)) {
        if (!hideContent) {
            BlogAutoSizeImage(
                modifier = Modifier.sharedBoundsBetweenScreen(
                    animatedScreenContentScope = animatedScreenContentScope,
                    key = imageUrl,
                ),
                imageUrl = imageUrl,
                description = media.description,
            )
        }
        if (media.type == BlogMediaType.GIFV) {
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center),
                imageVector = Icons.Default.PlayCircleOutline,
                tint = Color.White,
                contentDescription = "Play",
            )
        }
        if (showAlt && !media.description.isNullOrEmpty()) {
            var showBottomSheet by remember { mutableStateOf(false) }
            Surface(
                modifier = Modifier.align(Alignment.BottomStart)
                    .padding(start = 2.dp, bottom = 2.dp),
                onClick = {
                    showBottomSheet = true
                },
                shape = RoundedCornerShape(4.dp),
                enabled = true,
                color = Color.Black.copy(alpha = 0.6F),
                contentColor = Color.White,
                content = {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        text = "ALT",
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
            )

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 28.dp)
                            .wrapContentHeight()
                    ) {
                        Text(text = media.description.orEmpty())
                    }
                }
            }
        }
    }
}

@Composable
private fun BlogAutoSizeImage(
    modifier: Modifier,
    imageUrl: String?,
    description: String?,
) {
    AutoSizeImage(
        request = remember {
            ImageRequest {
                data(imageUrl)
            }
        },
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        contentDescription = description.ifNullOrEmpty { "Blog Image Media" },
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
