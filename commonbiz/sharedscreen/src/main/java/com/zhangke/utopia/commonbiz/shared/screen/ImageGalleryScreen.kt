package com.zhangke.utopia.commonbiz.shared.screen

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.zhangke.framework.composable.dpValue
import com.zhangke.framework.composable.photo.ExperimentalPhotoApi
import com.zhangke.framework.composable.photo.PhotoBox
import com.zhangke.framework.composable.photo.rememberPhotoState
import com.zhangke.framework.utils.aspectRatio
import com.zhangke.framework.utils.dpToPx
import com.zhangke.framework.utils.pxToDp
import com.zhangke.framework.utils.toPx
import com.zhangke.utopia.status.blog.BlogMedia
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

private const val animationDuration = 500
private const val doubleTapScale = 1.5F

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
                modifier = Modifier
                    .fillMaxSize(),
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

    @OptIn(ExperimentalPhotoApi::class)
    @Composable
    private fun ImagePage(
        media: BlogMedia,
        coordinates: LayoutCoordinates?,
        needAnimateIn: Boolean,
        animateInFinished: () -> Unit,
    ) {
        val photoState = rememberPhotoState()
        PhotoBox(
            modifier = Modifier
                .fillMaxSize(),
            state = photoState,
            contentAlignment = Alignment.TopStart,
        ) {
            val context = LocalContext.current
            val config = LocalConfiguration.current
            val initialWidth = config.screenWidthDp.dp
            var initialHeight: Dp? by remember {
                mutableStateOf(null)
            }

            if (initialHeight == null && coordinates != null) {
                val originSize = coordinates.size
                val aspectRatio = originSize.width.toFloat() / originSize.height.toFloat()
                initialHeight = initialWidth / aspectRatio
            }
            if (initialHeight == null) {
                LaunchedEffect(media) {
                    val aspectRatio = ImageRequest.Builder(context)
                        .data(media.url)
                        .size(50, 50)
                        .build()
                        .let { context.imageLoader.execute(it) }
                        .drawable
                        ?.aspectRatio() ?: 1F
                    initialHeight = initialWidth / aspectRatio
                }
            }

            if (initialHeight != null) {
//                val layoutState = remember(initialWidth, initialHeight) {
//                    ImageNodeLayoutState(initialWidth, initialHeight!!)
//                }
//                SetupInitialLayout(
//                    coordinates = coordinates,
//                    layoutState = layoutState,
//                    needAnimate = needAnimateIn,
//                    animateInFinished = animateInFinished,
//                )
                AsyncImage(
                    modifier = Modifier
                        .height(initialHeight!!)
                        .width(initialWidth),
//                        .offset(layoutState.offsetX.value, layoutState.offsetY.value)
//                        .height(layoutState.height.value)
//                        .width(layoutState.width.value),
                    model = media.url,
                    contentScale = ContentScale.Crop,
                    contentDescription = media.description,
                )
            }
        }
    }

    @Composable
    private fun SetupInitialLayout(
        coordinates: LayoutCoordinates?,
        layoutState: ImageNodeLayoutState,
        needAnimate: Boolean,
        animateInFinished: () -> Unit,
    ) {
        val targetWidth = layoutState.initialWidth.toPx()
        val targetHeight = layoutState.initialHeight.toPx()
        val density = LocalDensity.current
        val config = LocalConfiguration.current
        val screenHeight = config.screenHeightDp.dpToPx(density)
        val targetOffsetX = 0F
        val targetOffsetY = screenHeight / 2F - targetHeight / 2F

        if (coordinates == null || !needAnimate) {
            layoutState.width.value = targetWidth.pxToDp(density)
            layoutState.height.value = targetHeight.pxToDp(density)
            layoutState.offsetX.value = targetOffsetX.pxToDp(density)
            layoutState.offsetY.value = targetOffsetY.pxToDp(density)
            return
        }

        val originSize = coordinates.size
        val offset = coordinates.positionInRoot()

        val imageViewWidthAnimatable = remember {
            Animatable(originSize.width.toFloat())
        }

        LaunchedEffect(imageViewWidthAnimatable) {
            imageViewWidthAnimatable.animateTo(
                targetValue = targetWidth,
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

        layoutState.width.value = imageViewWidthAnimatable.dpValue
        layoutState.height.value = imageViewHeightAnimatable.dpValue
        layoutState.offsetX.value = xOffsetAnimatable.dpValue
        layoutState.offsetY.value = yOffsetAnimatable.dpValue
    }

    private fun Modifier.reactImageGesture(
        layoutState: ImageNodeLayoutState
    ): Modifier = composed {
        val doubleTapOffsetFlow = remember(layoutState) {
            MutableSharedFlow<Pair<Offset, Long>?>()
        }
        val doubleTapOffset by doubleTapOffsetFlow.collectAsState(initial = null)
        if (doubleTapOffset != null) {
            HandleImageDoubleTap(
                doubleTapOffset!!,
                doubleTapOffset!!.first,
                layoutState,
            )
        }
        val coroutineScope = rememberCoroutineScope()
        pointerInput(layoutState) {
            detectTapGestures(
                onDoubleTap = { offset ->
                    coroutineScope.launch {
                        doubleTapOffsetFlow.emit(offset to System.currentTimeMillis())
                    }
                }
            )
        }
    }

    @Composable
    private fun HandleImageDoubleTap(
        key: Any,
        tapOffset: Offset,
        layoutState: ImageNodeLayoutState,
    ) {
        val currentWidthDpValue = layoutState.width.value
        val currentHeightDpValue = layoutState.height.value
        val targetWidthDp: Dp
        val targetHeightDp: Dp
        if (layoutState.initialWidth == currentWidthDpValue) {
            targetWidthDp = currentWidthDpValue * doubleTapScale
            targetHeightDp = currentHeightDpValue * doubleTapScale
        } else {
            targetWidthDp = currentWidthDpValue / doubleTapScale
            targetHeightDp = currentHeightDpValue / doubleTapScale
        }
        val widthAnimatable = remember(key) {
            Animatable(currentWidthDpValue.value)
        }
        LaunchedEffect(key) {
            widthAnimatable.animateTo(
                targetValue = targetWidthDp.value,
                animationSpec = tween(animationDuration, easing = LinearOutSlowInEasing),
            )
        }
        val heightAnimatable = remember(key) {
            Animatable(currentHeightDpValue.value)
        }
        LaunchedEffect(key) {
            heightAnimatable.animateTo(
                targetHeightDp.value,
                animationSpec = tween(animationDuration, easing = LinearOutSlowInEasing),
            )
        }
        Log.d(
            "U_TEST",
            "${layoutState.width}:${layoutState.height} -> ${widthAnimatable.value.dp}:${heightAnimatable.value.dp}"
        )
        layoutState.width.value = widthAnimatable.value.dp
        layoutState.height.value = heightAnimatable.value.dp
    }

    private class ImageNodeLayoutState(val initialWidth: Dp, val initialHeight: Dp) {

        val height = mutableStateOf(0.dp)

        val width = mutableStateOf(0.dp)

        val offsetX = mutableStateOf(0.dp)

        val offsetY = mutableStateOf(0.dp)
    }
}
