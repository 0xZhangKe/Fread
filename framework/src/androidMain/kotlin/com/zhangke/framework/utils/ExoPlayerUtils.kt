package com.zhangke.framework.utils

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource

@androidx.media3.common.util.UnstableApi
fun Uri.toMediaSource(): MediaSource{
    val defaultDataSourceFactory = DefaultDataSource.Factory(appContext)
    val dataSourceFactory = DefaultDataSource.Factory(appContext, defaultDataSourceFactory)
    return ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(MediaItem.fromUri(this))
}
