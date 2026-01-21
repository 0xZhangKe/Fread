package com.zhangke.fread.status.ui.common

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

data class StatusSharedElementConfig(
    val wholeBlogEnabled: Boolean,
    val imageAttachmentEnabled: Boolean,
    val label: String,
) {

    fun buildImageKey(url: String): String {
        return buildKey(label, url)
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

        fun buildKey(label: String, url: String): String {
            return "$label-$url"
        }
    }
}

val LocalStatusSharedElementConfig: ProvidableCompositionLocal<StatusSharedElementConfig> =
    compositionLocalOf { StatusSharedElementConfig.default() }
