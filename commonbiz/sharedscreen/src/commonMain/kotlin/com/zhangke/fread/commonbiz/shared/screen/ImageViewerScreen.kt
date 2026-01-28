package com.zhangke.fread.commonbiz.shared.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult
import com.seiko.imageloader.option.SizeResolver
import com.seiko.imageloader.rememberImagePainter
import com.zhangke.framework.blurhash.blurhash
import com.zhangke.framework.composable.HorizontalPageIndicator
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.image.viewer.ImageViewer
import com.zhangke.framework.composable.image.viewer.ImageViewerDefault
import com.zhangke.framework.composable.image.viewer.rememberImageViewerState
import com.zhangke.framework.imageloader.executeSafety
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.sharedElement
import com.zhangke.framework.permission.RequireLocalStoragePermission
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.common.utils.LocalMediaFileHelper
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.asImageMetaOrNull
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
data class ImageViewerScreenNavKey(
    val selectedIndex: Int,
    val imageList: List<ImageViewerImage>,
    val sharedElementKey: String? = null,
) : NavKey

@Composable
fun ImageViewerScreen(
    selectedIndex: Int,
    imageList: List<ImageViewerImage>,
    sharedElementKey: String?,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    val backgroundCommonAlpha = 0.95F
    if (imageList.isEmpty()) {
        LaunchedEffect(imageList) { backStack.removeLastOrNull() }
        return
    }
    var backgroundColorAlpha by remember {
        mutableFloatStateOf(0F)
    }
    var showIndicator by remember {
        mutableStateOf(false)
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
        modifier = Modifier.fillMaxSize(),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color.Black,
                alpha = backgroundColorAlpha,
            )
        }
        val pagerState = rememberPagerState(
            initialPage = selectedIndex.coerceAtLeast(0),
            pageCount = imageList::size,
        )

        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
            ) { pageIndex ->
                val currentMedia = imageList[pageIndex]
                ImagePageContent(
                    image = currentMedia,
                    sharedElementKey = if (pageIndex == selectedIndex) sharedElementKey else null,
                    onDismissRequest = backStack::removeLastOrNull,
                )
            }

            ImageTopBar(imageList[pagerState.currentPage])

            LaunchedEffect(Unit) {
                delay(300)
                showIndicator = true
            }
            if (showIndicator && pagerState.pageCount > 1) {
                HorizontalPageIndicator(
                    currentIndex = pagerState.currentPage,
                    pageCount = pagerState.pageCount,
                    modifier = Modifier
                        .padding(bottom = 64.dp)
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun ImagePageContent(
    image: ImageViewerImage,
    sharedElementKey: String?,
    onDismissRequest: () -> Unit,
) {
    val imageLoader = LocalImageLoader.current
    var aspectRatio: Float? by remember { mutableStateOf(image.aspect) }
    if (aspectRatio == null) {
        LaunchedEffect(image) {
            val request = ImageRequest {
                data(image.url)
                size(SizeResolver(Size(50f, 50f)))
            }
            aspectRatio = imageLoader.executeSafety(request).aspectRatio() ?: 1F
        }
    }
    if (aspectRatio != null) {
        val viewerState = rememberImageViewerState(
            aspectRatio = aspectRatio!!,
            onDismissRequest = onDismissRequest,
        )
        ImageViewer(
            state = viewerState,
            modifier = Modifier.fillMaxSize(),
        ) {
            val request = remember(image.url) {
                ImageRequest(image.url)
            }
            Image(
                painter = rememberImagePainter(request = request),
                modifier = Modifier
                    .fillMaxSize()
                    .blurhash(image.blurhash)
                    .let {
                        if (sharedElementKey.isNullOrEmpty()) {
                            it
                        } else {
                            it.sharedElement(sharedElementKey)
                        }
                    },
                contentScale = ContentScale.FillBounds,
                contentDescription = image.description,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxScope.ImageTopBar(image: ImageViewerImage) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val mediaFileHelper = LocalMediaFileHelper.current
    Row(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .statusBarsPadding()
            .padding(top = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var needSaveImage by remember { mutableStateOf(false) }
        if (needSaveImage) {
            RequireLocalStoragePermission(
                onPermissionGranted = {
                    mediaFileHelper.saveImageToGallery(image.url)
                    needSaveImage = false
                },
                onPermissionDenied = {
                    needSaveImage = false
                },
            )
        }
        Toolbar.DownloadButton(
            onClick = {
                needSaveImage = true
            },
            tint = Color.White.copy(alpha = 0.7F),
        )
        if (!image.description.isNullOrEmpty()) {
            Spacer(modifier = Modifier.width(16.dp))
            SimpleIconButton(
                onClick = { showBottomSheet = true },
                tint = Color.White.copy(alpha = 0.7F),
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
                        Text(text = image.description)
                    }
                }
            }
        }
    }
}

@Serializable
data class ImageViewerImage(
    val url: String,
    val previewUrl: String? = null,
    val description: String? = null,
    val blurhash: String? = null,
    val aspect: Float? = null,
) : PlatformSerializable

fun BlogMedia.toImage(): ImageViewerImage {
    return ImageViewerImage(
        url = this.url,
        previewUrl = this.previewUrl,
        description = this.description,
        blurhash = this.blurhash,
        aspect = this.meta?.asImageMetaOrNull()?.original?.aspect,
    )
}

fun List<BlogMedia>.toImages(): List<ImageViewerImage> {
    return this.map { it.toImage() }
}

internal expect fun ImageResult.aspectRatio(): Float?
