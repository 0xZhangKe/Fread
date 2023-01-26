package com.zhangke.utopia.blogprovider

import com.google.gson.JsonObject

abstract class BlogSource(
    /**
     * This source`s source or rather this parent source info
     */
    val metaSourceInfo: MetaSourceInfo,
    val sourceServer: String,
    val protocol: String,
    val sourceName: String,
    val sourceDescription: String?,
    val avatar: String?,
    val extra: JsonObject?
)