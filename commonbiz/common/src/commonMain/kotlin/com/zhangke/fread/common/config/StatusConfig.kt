package com.zhangke.fread.common.config

data class StatusConfig(
    val alwaysShowSensitiveContent: Boolean,
    val contentSize: StatusContentSize,
    val immersiveNavBar: Boolean,
) {

    companion object {

        fun default(
            alwaysShowSensitiveContent: Boolean = false,
            contentSize: StatusContentSize = StatusContentSize.default(),
            immersiveNavBar: Boolean = true,
        ) = StatusConfig(
            alwaysShowSensitiveContent = alwaysShowSensitiveContent,
            contentSize = contentSize,
            immersiveNavBar = immersiveNavBar,
        )
    }
}
