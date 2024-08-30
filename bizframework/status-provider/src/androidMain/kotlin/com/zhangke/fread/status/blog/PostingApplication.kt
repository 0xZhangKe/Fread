package com.zhangke.fread.status.blog

import kotlinx.serialization.Serializable

@Serializable
data class PostingApplication(
    val name: String,
    val website: String?,
) : java.io.Serializable
