package com.zhangke.fread.common.config

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalStatusConfig: ProvidableCompositionLocal<StatusConfig?> =
    compositionLocalOf { null }

data class StatusConfig(
    val alwaysShowSensitiveContent: Boolean,
    val contentSize: StatusContentSize,
) {

    companion object {

        fun default(
            alwaysShowSensitiveContent: Boolean = false,
            contentSize: StatusContentSize = StatusContentSize.default(),
        ) = StatusConfig(
            alwaysShowSensitiveContent = alwaysShowSensitiveContent,
            contentSize = contentSize,
        )
    }
}
