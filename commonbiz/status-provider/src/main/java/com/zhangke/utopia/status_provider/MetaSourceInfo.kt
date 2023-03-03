package com.zhangke.utopia.status_provider

import com.google.gson.JsonObject

data class MetaSourceInfo(
    val url: String,
    val name: String,
    val thumbnail: String?,
    val description: String?,
    val extra: JsonObject? = null
)