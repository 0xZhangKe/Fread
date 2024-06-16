package com.zhangke.fread.status.ui.richtext.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.zhangke.framework.composable.freadPlaceholder

@Composable
fun EmojiImage(
    uri: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val painter =
        rememberAsyncImagePainter(
            model =
            remember(uri, context) {
                ImageRequest.Builder(context)
                    .data(uri)
                    .size(Size.ORIGINAL)
                    .build()
            },
        )
    if (painter.state is AsyncImagePainter.State.Success) {
        val aspectRatio =
            remember(painter.intrinsicSize) {
                val size = painter.intrinsicSize
                (size.width / size.height).takeUnless { it.isNaN() } ?: 1f
            }
        Image(
            painter = painter,
            contentDescription = null,
            modifier =
            modifier
                .aspectRatio(aspectRatio)
                .fillMaxSize(),
        )
    } else {
        Box(
            modifier =
            modifier
                .size(24.dp)
                .freadPlaceholder(true),
        )
    }
}
