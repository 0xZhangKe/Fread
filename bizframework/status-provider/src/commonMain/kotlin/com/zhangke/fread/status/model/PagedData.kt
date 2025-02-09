package com.zhangke.fread.status.model

import kotlinx.serialization.Serializable

@Serializable
data class PagedData<T>(
    val list: List<T>,
    val cursor: String?,
)
