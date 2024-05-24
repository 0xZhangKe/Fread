package com.zhangke.utopia.commonbiz.shared.screen

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.zhangke.framework.blurhash.blurhash
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.image.viewer.ImageViewer
import com.zhangke.framework.composable.image.viewer.ImageViewerDefault
import com.zhangke.framework.composable.image.viewer.rememberImageViewerState
import com.zhangke.framework.media.MediaFileHelper
import com.zhangke.framework.permission.RequireLocalStoragePermission
import com.zhangke.framework.utils.aspectRatio
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.asImageMetaOrNull
import kotlinx.coroutines.launch

class ImageViewerScreen(
    private val selectedIndex: Int,
    private val mediaList: List<BlogMedia>,
    @Transient private val coordinatesList: List<LayoutCoordinates?> = emptyList(),
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
                    ImageViewerDefault.ANIMATION_DURATION,
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

            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = pagerState,
                ) { pageIndex ->
                    val currentMedia = mediaList[pageIndex]
                    val coordinates = coordinatesList[pageIndex]
                    val animatedIn = pageIndex == selectedIndex && animatedInHolder.first().not()
                    ImagePageContent(
                        media = currentMedia,
                        coordinates = coordinates,
                        needAnimateIn = animatedIn,
                        animateInFinished = {
                            animatedInHolder[0] = true
                        },
                        onDismissRequest = {
                            navigator.pop()
                        },
                        onStartDismiss = {
                            coroutineScope.launch {
                                Animatable(backgroundCommonAlpha).animateTo(
                                    targetValue = 0.1F,
                                    animationSpec = tween(
                                        ImageViewerDefault.ANIMATION_DURATION,
                                        easing = FastOutSlowInEasing
                                    ),
                                ) {
                                    backgroundColorAlpha = value
                                }
                            }
                        }
                    )
                }

                ImageTopBar(media = mediaList[pagerState.currentPage])
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
            val viewerState = if (coordinates != null) {
                rememberImageViewerState(
                    aspectRatio = aspectRatio!!,
                    needAnimateIn = needAnimateIn,
                    initialSize = coordinates.size.toSize(),
                    initialOffset = coordinates.positionInRoot(),
                    onAnimateInFinished = animateInFinished,
                    onDismissRequest = onDismissRequest,
                    onStartDismiss = onStartDismiss,
                )
            } else {
                rememberImageViewerState(
                    aspectRatio = aspectRatio!!,
                    needAnimateIn = false,
                    onDismissRequest = onDismissRequest,
                )
            }
            Log.d("U_TEST", "viewerState: $viewerState")
            ImageViewer(
                state = viewerState,
                modifier = Modifier.fillMaxSize(),
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .run {
                            if (media.blurhash
                                    .isNullOrEmpty()
                                    .not()
                            ) {
                                blurhash(media.blurhash!!)
                            } else {
                                this
                            }
                        },
                    model = media.url,
                    contentScale = ContentScale.FillBounds,
                    contentDescription = media.description,
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BoxScope.ImageTopBar(media: BlogMedia) {
        var showBottomSheet by remember { mutableStateOf(false) }
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var needSaveImage by remember { mutableStateOf(false) }
            if (needSaveImage) {
                RequireLocalStoragePermission(
                    onPermissionGranted = {
                        MediaFileHelper.saveImageToGallery(context, media.url)
                        needSaveImage = false
                    },
                    onPermissionDenied = {
                        needSaveImage = false
                    },
                )
            }
            SimpleIconButton(
                onClick = {
                    needSaveImage = true
                },
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                imageVector = Icons.Default.Download,
                contentDescription = "Download",
            )
            if (media.description != null) {
                Spacer(modifier = Modifier.width(16.dp))
                SimpleIconButton(
                    onClick = {
                        showBottomSheet = true
                    },
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    imageVector = Icons.Default.Info,
                    contentDescription = "Image description",
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
}
