package com.zhangke.fread.common.config

data class StatusConfig(
    val alwaysShowSensitiveContent: Boolean,
    val contentSize: StatusContentSize,
    val immersiveNavBar: Boolean,
    val homeTabNextButtonVisible: Boolean,
    val homeTabRefreshButtonVisible: Boolean,
) {

    companion object {

        fun default(
            alwaysShowSensitiveContent: Boolean = false,
            contentSize: StatusContentSize = StatusContentSize.default(),
            immersiveNavBar: Boolean = true,
            homeTabNextButtonVisible: Boolean = true,
            homeTabRefreshButtonVisible: Boolean = true,
        ) = StatusConfig(
            alwaysShowSensitiveContent = alwaysShowSensitiveContent,
            contentSize = contentSize,
            immersiveNavBar = immersiveNavBar,
            homeTabNextButtonVisible = homeTabNextButtonVisible,
            homeTabRefreshButtonVisible = homeTabRefreshButtonVisible,
        )
    }
}
