package com.zhangke.utopia.commonbiz.shared.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.zhangke.framework.composable.image.viewer.ImageViewer
import com.zhangke.framework.composable.image.viewer.ImageViewerDefault
import com.zhangke.framework.composable.image.viewer.rememberImageViewerState
import com.zhangke.framework.utils.aspectRatio
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.asImageMetaOrNull
import kotlinx.coroutines.launch

class ImageViewerScreen(
    private val selectedIndex: Int,
    private val mediaList: List<BlogMedia>,
    private val coordinatesList: List<LayoutCoordinates?>,
    private val onDismiss: () -> Unit,
) : Screen {

    private val backgroundCommonAlpha = 0.95F

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        var backgroundColorAlpha by remember {
            mutableFloatStateOf(0F)
        }
        LaunchedEffect(Unit) {
            Animatable(0F).animateTo(
                targetValue = backgroundCommonAlpha,
                animationSpec = tween(
                    ImageViewerDefault.animationDuration,
                    easing = FastOutSlowInEasing
                ),
            ) {
                backgroundColorAlpha = value
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            // TODO: check this bug, maybe cause Compose.
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    color = Color.Black,
                    alpha = backgroundColorAlpha,
                )
            }
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
                    onStartDismiss = {
                        coroutineScope.launch {
                            Animatable(backgroundCommonAlpha).animateTo(
                                targetValue = 0.1F,
                                animationSpec = tween(
                                    ImageViewerDefault.animationDuration,
                                    easing = FastOutSlowInEasing
                                ),
                            ) {
                                backgroundColorAlpha = value
                            }
                        }
                    }
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
        onDismissRequest: () -> Unit,
        onStartDismiss: () -> Unit,
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
                onStartDismiss = onStartDismiss,
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
