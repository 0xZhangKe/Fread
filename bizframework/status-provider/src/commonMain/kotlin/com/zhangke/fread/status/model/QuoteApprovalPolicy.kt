package com.zhangke.fread.status.model

import kotlinx.serialization.Serializable

@Serializable
enum class QuoteApprovalPolicy {
    PUBLIC,
    FOLLOWERS,
    FOLLOWING,
    NOBODY,
}
