package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonObject
import com.zhangke.utopia.status_provider.MetaSourceInfo

internal class BlogSourceScope(
    val metaSourceInfo: MetaSourceInfo,
    val uri: String,
    val protocol: String,
    val sourceName: String,
    val sourceDescription: String?,
    val avatar: String?,
    val extra: JsonObject
)
