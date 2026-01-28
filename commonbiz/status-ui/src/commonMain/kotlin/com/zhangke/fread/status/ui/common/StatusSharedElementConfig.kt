package com.zhangke.fread.status.ui.common

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

data class StatusSharedElementConfig(
    val wholeBlogEnabled: Boolean,
    val imageAttachmentEnabled: Boolean,
    val label: String,
) {

    fun buildImageKey(key: String): String {
        return buildKey(label, key)
    }

    companion object {

        fun default(): StatusSharedElementConfig {
            return StatusSharedElementConfig(
                wholeBlogEnabled = true,
                imageAttachmentEnabled = true,
                label = "feeds"
            )
        }

        fun buildKey(label: String, key: String): String {
            return "$label-$key"
        }
    }
}

val LocalStatusSharedElementConfig: ProvidableCompositionLocal<StatusSharedElementConfig> =
    compositionLocalOf { StatusSharedElementConfig.default() }
