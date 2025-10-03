package com.zhangke.fread.status.blog

import kotlinx.serialization.Serializable

@Serializable
enum class CurrentUserQuoteApproval {

    AUTOMATIC,
    MANUAL,
    DENIED,
    UNKNOWN;

    val quotable: Boolean get() = this == AUTOMATIC || this == MANUAL
}
