package com.zhangke.utopia.blogprovider

import com.google.gson.JsonObject

data class MetaSourceInfo(
    val url: String,
    val name: String,
    val thumbnail: String?,
    val description: String?,
    val extra: JsonObject? = null
)