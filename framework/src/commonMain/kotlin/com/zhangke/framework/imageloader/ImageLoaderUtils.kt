package com.zhangke.framework.imageloader

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult

suspend fun ImageLoader.executeSafety(request: ImageRequest): ImageResult {
    return try {
        this.execute(request)
    } catch (e: Throwable) {
        ImageResult.OfError(e)
    }
}
