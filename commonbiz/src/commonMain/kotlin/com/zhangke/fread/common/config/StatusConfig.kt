package com.zhangke.fread.common.config

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
