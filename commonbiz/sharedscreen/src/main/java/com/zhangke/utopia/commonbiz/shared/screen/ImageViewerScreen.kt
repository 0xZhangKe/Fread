package com.zhangke.utopia.commonbiz.shared.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.zhangke.framework.composable.image.viewer.ImageViewer
import com.zhangke.framework.composable.image.viewer.rememberImageViewerState
import com.zhangke.framework.utils.aspectRatio
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.asImageMetaOrNull

class ImageViewerScreen(
    private val selectedIndex: Int,
    private val mediaList: List<BlogMedia>,
    private val coordinatesList: List<LayoutCoordinates?>,
    private val onDismiss: () -> Unit,
) : AndroidScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val backgroundAlpha = remember {
            Animatable(0F)
        }
        LaunchedEffect(backgroundAlpha) {
            backgroundAlpha.animateTo(
                targetValue = 0.95F,
                animationSpec = tween(500, easing = FastOutSlowInEasing),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(backgroundAlpha.value))
        ) {
            val pagerState = rememberPagerState(
                initialPage = selectedIndex,
                pageCount = mediaList::size,
            )
            val animatedInHolder = remember {
                arrayOf(false)
            }
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
            ) { pageIndex ->
                val currentMedia = mediaList[pageIndex]
                val coordinates = coordinatesList[pageIndex]
                val animatedIn = pageIndex == selectedIndex && animatedInHolder.first().not()
                ImagePageContent(
                    currentMedia,
                    coordinates,
                    animatedIn,
                    animateInFinished = {
                        animatedInHolder[0] = true
                    },
                    onDismissRequest = {
                        navigator.pop()
                        onDismiss()
                    },
                )
            }
        }
    }

    @Composable
    private fun ImagePageContent(
        media: BlogMedia,
        coordinates: LayoutCoordinates?,
        needAnimateIn: Boolean,
        animateInFinished: () -> Unit,
        onDismissRequest: () -> Unit
    ) {
        val context = LocalContext.current
        var aspectRatio: Float? by remember {
            mutableStateOf(media.meta?.asImageMetaOrNull()?.original?.aspect)
        }
        if (aspectRatio == null) {
            LaunchedEffect(media) {
                aspectRatio = ImageRequest.Builder(context)
                    .data(media.url)
                    .size(50, 50)
                    .build()
                    .let { context.imageLoader.execute(it) }
                    .drawable
                    ?.aspectRatio() ?: 1F
            }
        }
        if (aspectRatio != null) {
            val viewerState = if (coordinates != null && needAnimateIn) {
                rememberImageViewerState(
                    aspectRatio = aspectRatio!!,
                    initialSize = coordinates.size.toSize(),
                    initialOffset = coordinates.positionInRoot(),
                ).also {
                    it.onAnimateInFinished = animateInFinished
                }
            } else {
                rememberImageViewerState(aspectRatio = aspectRatio!!)
            }
            ImageViewer(
                state = viewerState,
                modifier = Modifier.fillMaxSize(),
                onDismissRequest = onDismissRequest,
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    model = media.url,
                    contentScale = ContentScale.FillBounds,
                    contentDescription = media.description,
                )
            }
        }
    }
}
