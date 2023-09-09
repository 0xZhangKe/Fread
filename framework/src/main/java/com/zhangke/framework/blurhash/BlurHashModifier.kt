package com.zhangke.framework.blurhash

import android.graphics.Bitmap
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import kotlin.math.roundToInt

fun Modifier.blurhash(blurHash: String): Modifier = composed {
    var size: Size? by remember {
        mutableStateOf(null)
    }
    var bitmap: Bitmap? by remember {
        mutableStateOf(null)
    }
    if (size != null) {
        DisposableEffect(blurHash) {

            if (bitmap == null && size != null) {
                bitmap = BlurHashDecoder.decode(
                    blurHash = blurHash,
                    width = (size!!.width * 0.5F).roundToInt(),
                    height = (size!!.height * 0.5F).roundToInt(),
                )
            }

            onDispose {
                if (bitmap?.isRecycled == false) {
                    bitmap?.recycle()
                }
                bitmap = null
            }
        }
    }
    onSizeChanged {
        size = it.toSize()
    }.drawBehind {
        if (bitmap != null && size != null) {
            drawImage(
                image = bitmap!!.asImageBitmap(),
//                destSize = size!!,
            )
        }
    }
}
