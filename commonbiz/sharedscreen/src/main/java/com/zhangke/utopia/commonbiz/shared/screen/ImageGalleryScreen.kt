package com.zhangke.utopia.commonbiz.shared.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import cafe.adriel.voyager.androidx.AndroidScreen
import coil.compose.AsyncImage
import com.zhangke.framework.composable.dpValue
import com.zhangke.framework.utils.dpToPx
import com.zhangke.framework.utils.pxToDp
import com.zhangke.utopia.status.blog.BlogMedia
import kotlinx.coroutines.delay

private const val animationDuration = 500

class ImageGalleryScreen(
    private val selectedIndex: Int,
    private val mediaList: List<BlogMedia>,
    private val coordinatesList: List<LayoutCoordinates?>,
    private val onDismiss: () -> Unit,
) : AndroidScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val backgroundAlpha = remember {
            Animatable(0F)
        }
        LaunchedEffect(backgroundAlpha) {
            backgroundAlpha.animateTo(
                targetValue = 0.95F,
                animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(backgroundAlpha.value))
        ) {
            val pagerState = rememberPagerState(selectedIndex)
            val animatedInHolder = remember {
                arrayOf(false)
            }
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                pageCount = mediaList.size,
                state = pagerState,
            ) { pageIndex ->
                val currentMedia = mediaList[pageIndex]
                val coordinates = coordinatesList[pageIndex]
                val animatedIn = pageIndex == selectedIndex && animatedInHolder.first().not()
                ImagePage(
                    currentMedia,
                    coordinates,
                    animatedIn,
                    animateInFinished = {
                        animatedInHolder[0] = true
                    }
                )
            }
        }
    }

    @Composable
    private fun ImagePage(
        media: BlogMedia,
        coordinates: LayoutCoordinates?,
        needAnimateIn: Boolean,
        animateInFinished: () -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                modifier = Modifier
                    .setupInitialLayout(coordinates, needAnimateIn, animateInFinished)
                    .scrollable(rememberScrollState(), Orientation.Vertical),
                model = media.url,
                contentScale = ContentScale.Crop,
                contentDescription = media.description,
            )
        }
    }

    private fun Modifier.setupInitialLayout(
        coordinates: LayoutCoordinates?,
        needAnimate: Boolean,
        animateInFinished: () -> Unit,
    ): Modifier = composed {
        if (coordinates == null) return@composed this
        val density = LocalDensity.current
        val originSize = coordinates.size
        val aspectRatio = originSize.width.toFloat() / originSize.height.toFloat()
        val offset = coordinates.positionInRoot()
        val config = LocalConfiguration.current
        val screenWidth = config.screenWidthDp.dpToPx(density)
        val screenHeight = config.screenHeightDp.dpToPx(density)

        val targetHeight = screenWidth / aspectRatio
        val targetOffsetX = 0F
        val targetOffsetY = screenHeight / 2F - targetHeight / 2F

        if (!needAnimate) {
            return@composed width(screenWidth.pxToDp(density))
                .height(targetHeight.pxToDp(density))
                .offset(x = targetOffsetX.pxToDp(density), y = targetOffsetY.pxToDp(density))
        }

        val imageViewWidthAnimatable = remember {
            Animatable(originSize.width.toFloat())
        }

        LaunchedEffect(imageViewWidthAnimatable) {
            imageViewWidthAnimatable.animateTo(
                targetValue = screenWidth,
                animationSpec = tween(animationDuration, easing = LinearOutSlowInEasing),
            )
        }
        val imageViewHeightAnimatable = remember {
            Animatable(originSize.height.toFloat())
        }
        LaunchedEffect(imageViewHeightAnimatable) {
            imageViewHeightAnimatable.animateTo(
                targetValue = targetHeight,
                animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
            )
        }
        val xOffsetAnimatable = remember {
            Animatable(offset.x)
        }
        LaunchedEffect(xOffsetAnimatable) {
            xOffsetAnimatable.animateTo(
                targetValue = targetOffsetX,
                animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
            )
        }
        val yOffsetAnimatable = remember {
            Animatable(offset.y)
        }
        LaunchedEffect(yOffsetAnimatable) {
            yOffsetAnimatable.animateTo(
                targetValue = targetOffsetY,
                animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
            )
        }
        LaunchedEffect(Unit) {
            delay(animationDuration.toLong())
            animateInFinished()
        }
        width(imageViewWidthAnimatable.dpValue)
            .height(imageViewHeightAnimatable.dpValue)
            .offset(x = xOffsetAnimatable.dpValue, y = yOffsetAnimatable.dpValue)
    }
}
