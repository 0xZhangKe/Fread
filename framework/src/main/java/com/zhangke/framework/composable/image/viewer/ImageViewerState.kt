package com.zhangke.framework.composable.image.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

@Composable
fun rememberImageViewerState(
    aspectRatio: Float,
    initialSize: Size = Size.Unspecified,
    initialOffset: Offset = Offset.Zero,
    minimumScale: Float = 1f,
    maximumScale: Float = 3f,
): ImageViewerState = rememberSaveable(saver = ImageViewerState.Saver) {
    ImageViewerState(
        aspectRatio = aspectRatio,
        initialSize = initialSize,
        initialOffset = initialOffset,
        minimumScale = minimumScale,
        maximumScale = maximumScale,
    )
}


@Stable
class ImageViewerState(
    private val aspectRatio: Float,
    private val initialSize: Size,
    private val initialOffset: Offset,
    private val minimumScale: Float = 1f,
    private val maximumScale: Float = 3f,
) {


    internal companion object {

        val Saver: Saver<ImageViewerState, *> = listSaver(
            save = {
                listOf<Float>(
                    it.aspectRatio,
                    it.initialSize.height,
                    it.initialSize.width,
                    it.initialOffset.x,
                    it.initialOffset.y,
                    it.minimumScale,
                    it.maximumScale,
                )
            },
            restore = {
                ImageViewerState(
                    aspectRatio = it[0],
                    initialSize = Size(width = it[1], height = it[2]),
                    initialOffset = Offset(
                        x = it[3],
                        y = it[4],
                    ),
                    minimumScale = it[5],
                    maximumScale = it[6],
                )
            }
        )
    }
}
