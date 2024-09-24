package com.zhangke.fread.status.ui.style

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.zhangke.fread.common.config.StatusConfig
import com.zhangke.fread.common.config.StatusContentSize

val LocalStatusUiConfig: ProvidableCompositionLocal<StatusUiConfig> =
    compositionLocalOf { error("LocalStatusUiConfig not init!") }

data class StatusUiConfig(
    val alwaysShowSensitiveContent: Boolean,
    val contentStyle: StatusStyle,
) {

    companion object {

        @Composable
        fun create(
            config: StatusConfig,
        ): StatusUiConfig {
            return StatusUiConfig(
                alwaysShowSensitiveContent = config.alwaysShowSensitiveContent,
                contentStyle = config.contentSize.toStyle(),
            )
        }

        @Composable
        private fun StatusContentSize.toStyle(): StatusStyle {
            return when (this) {
                StatusContentSize.SMALL -> StatusStyles.small()
                StatusContentSize.MEDIUM -> StatusStyles.medium()
                StatusContentSize.LARGE -> StatusStyles.large()
            }
        }
    }
}
