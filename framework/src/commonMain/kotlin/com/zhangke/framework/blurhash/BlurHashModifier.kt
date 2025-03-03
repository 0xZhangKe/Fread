package com.zhangke.framework.blurhash

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

fun Modifier.blurhash(blurHash: String?): Modifier = composed {
    if (blurHash.isNullOrEmpty()) {
        val placeholderColor = MaterialTheme.colorScheme.surfaceDim
        return@composed this.drawBehind {
            drawRect(color = placeholderColor)
        }
    }
    var size: Size? by remember(blurHash) {
        mutableStateOf(null)
    }
    var bitmap: ImageBitmap? by remember(blurHash) {
        mutableStateOf(null)
    }
    val coroutineScope = rememberCoroutineScope()
    if (size != null) {
        DisposableEffect(blurHash) {

            if (bitmap == null && size != null) {
                coroutineScope.launch {
                    bitmap = withContext(Dispatchers.IO) {
                        BlurHashDecoder.decode(
                            blurHash = blurHash,
                            width = (size!!.width * 0.5F).roundToInt(),
                            height = (size!!.height * 0.5F).roundToInt(),
                            useCache = false,
                        )
                    }
                }
            }

            onDispose {
                // TODO: check is this bitmap need to recycler and earlier recycle
                bitmap = null
            }
        }
    }
    onSizeChanged {
        size = it.toSize()
    }.drawBehind {
        if (bitmap != null && size != null) {
            drawImage(
                image = bitmap!!,
                dstSize = IntSize(size!!.width.roundToInt(), size!!.height.roundToInt()),
            )
        }
    }
}
