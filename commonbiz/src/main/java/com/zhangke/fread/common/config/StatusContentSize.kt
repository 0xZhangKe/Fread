package com.zhangke.fread.common.config

enum class StatusContentSize {

    SMALL,
    MEDIUM,
    LARGE;

    companion object {

        fun default(): StatusContentSize = MEDIUM
    }
}
