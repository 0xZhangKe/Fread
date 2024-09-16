package com.zhangke.fread.status.ui.richtext.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.zhangke.framework.composable.freadPlaceholder

@Composable
fun EmojiImage(
    uri: String,
    modifier: Modifier = Modifier,
) {
    val action = rememberImageAction(uri)
    if (action is ImageAction.Success) {
        val painter = rememberImageSuccessPainter(action)
        val aspectRatio = remember(painter) {
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
