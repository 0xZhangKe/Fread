package com.zhangke.fread.status.ui.video

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.media3.exoplayer.ExoPlayer

val LocalInlineVideoPlayer: ProvidableCompositionLocal<ExoPlayer?> =
    staticCompositionLocalOf { null }

fun provideExoPlayer(){

}
