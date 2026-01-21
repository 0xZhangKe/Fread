package com.zhangke.fread.status.ui.common

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.zhangke.framework.utils.Log

data class StatusSharedElementConfig(
    val wholeBlogEnabled: Boolean,
    val imageAttachmentEnabled: Boolean,
    val label: String,
) {

    fun buildImageKey(url: String): String {
        return "$label-$url".also {
            Log.d("Z_TEST") { "image key: $it" }
        }
    }

    fun buildBlogKey(id: String): String {
        return "$label-$id"
    }

    companion object {

        fun default(): StatusSharedElementConfig {
            return StatusSharedElementConfig(
                wholeBlogEnabled = true,
                imageAttachmentEnabled = true,
                label = "feeds"
            )
        }
    }
}

val LocalStatusSharedElementConfig: ProvidableCompositionLocal<StatusSharedElementConfig> =
    compositionLocalOf { StatusSharedElementConfig.default() }
